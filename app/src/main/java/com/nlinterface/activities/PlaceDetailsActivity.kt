package com.nlinterface.activities

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.nlinterface.R
import com.nlinterface.adapters.PlaceDetailsAdapter
import com.nlinterface.databinding.ActivityPlaceDetailsBinding
import com.nlinterface.dataclasses.PlaceDetailsItem
import com.nlinterface.interfaces.PlaceDetailsItemCallback
import com.nlinterface.utility.ActivityType
import com.nlinterface.utility.TextToSpeechUtility
import com.nlinterface.utility.setViewRelativeSize
import com.nlinterface.viewmodels.PlaceDetailsViewModel
import java.util.Locale


class PlaceDetailsActivity: AppCompatActivity(), PlaceDetailsItemCallback {

    private lateinit var binding: ActivityPlaceDetailsBinding
    private lateinit var viewModel: PlaceDetailsViewModel
    private lateinit var placeDetailsItemList: ArrayList<PlaceDetailsItem>
    private lateinit var adapter: PlaceDetailsAdapter
    private lateinit var voiceActivationButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlaceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[PlaceDetailsViewModel::class.java]
        viewModel.initPlaceClient(this)

        viewModel.fetchPlaceDetailsItemList()

        configureUI()
        configureAutocompleteFragment()
        configureTTS()
        configureSTT()

    }

    private fun configureTTS() {

        viewModel.initTTS()

        val ttsInitializedObserver = Observer<Boolean> { _ ->
            viewModel.say(resources.getString(R.string.place_details))
        }

        viewModel.ttsInitialized.observe(this, ttsInitializedObserver)

    }

    private fun configureSTT() {

        viewModel.initSTT()

        val sttIsListeningObserver = Observer<Boolean> { isListening ->
            if (isListening) {
                voiceActivationButton.setImageResource(R.drawable.ic_mic_green)
            } else {
                voiceActivationButton.setImageResource(R.drawable.ic_mic_white)
            }
        }

        viewModel.isListening.observe(this, sttIsListeningObserver)

        val commandObserver = Observer<ArrayList<String>> {command ->
            executeCommand(command)
        }

        viewModel.command.observe(this, commandObserver)

    }

    private fun executeCommand(command: ArrayList<String>?) {

        if (command != null && command.size == 3) {
            if (command[0] == "GOTO") {
                navToActivity(command[1])
            } else {
                viewModel.say(resources.getString(R.string.choose_activity_to_navigate_to))
            }
        }

    }

    private fun navToActivity(activity: String) {

        when (activity) {

            ActivityType.PLACEDETAILS.toString() -> {
                viewModel.say(resources.getString(R.string.place_details))
            }

            ActivityType.MAIN.toString() -> {
                val intent = Intent(this, MainActivity::class.java)
                this.startActivity(intent)
            }
            ActivityType.GROCERYLIST.toString() -> {
                val intent = Intent(this, GroceryListActivity::class.java)
                this.startActivity(intent)
            }
            ActivityType.SETTINGS.toString() -> {
                val intent = Intent(this, SettingsActivity::class.java)
                this.startActivity(intent)
            }

        }
    }

    private fun configureUI() {

        // set up voice activation button listener
        voiceActivationButton = findViewById<View>(R.id.voice_activation_bt) as ImageButton
        voiceActivationButton.setOnClickListener {
            onVoiceActivationButtonClick()
        }

        // resize Voice Activation Button to 1/3 of display size
        setViewRelativeSize(voiceActivationButton, 1.0, 0.33)

        configureRecyclerView()
    }

    private fun configureRecyclerView() {

        val rvPlaceDetails = findViewById<View>(R.id.place_details_rv) as RecyclerView
        placeDetailsItemList = viewModel.placeDetailsItemList

        adapter = PlaceDetailsAdapter(placeDetailsItemList, this)
        rvPlaceDetails.adapter = adapter
        rvPlaceDetails.layoutManager = LinearLayoutManager(this)

        rvPlaceDetails.itemAnimator?.changeDuration = 0

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {return false}

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val placeDetailsItem: PlaceDetailsItem =
                    placeDetailsItemList[viewHolder.adapterPosition]

                val index = viewHolder.adapterPosition

                viewModel.deletePlaceDetailsItem(placeDetailsItem)
                adapter.notifyItemRemoved(index)
                viewModel.say(resources.getString(R.string.deleted_ITEMNAME_from_saved_places, placeDetailsItem.storeName))
            }
        }).attachToRecyclerView(rvPlaceDetails)

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {return false}

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val placeDetailsItem: PlaceDetailsItem =
                    placeDetailsItemList[viewHolder.adapterPosition]

                val index = viewHolder.adapterPosition

                viewModel.deletePlaceDetailsItem(placeDetailsItem)
                adapter.notifyItemRemoved(index)
                viewModel.say(resources.getString(R.string.deleted_ITEMNAME_from_saved_places, placeDetailsItem.storeName))
            }
        }).attachToRecyclerView(rvPlaceDetails)

    }

    private fun configureAutocompleteFragment() {

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID))
        autocompleteFragment.setTypesFilter(listOf("supermarket"))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(status: Status) {
                viewModel.onError(status)
            }
            override fun onPlaceSelected(place: Place) {
                viewModel.onPlaceSelected(place) {
                    if (it) {
                        adapter.notifyItemInserted(placeDetailsItemList.size - 1)
                        val storeName = placeDetailsItemList.last().storeName
                        viewModel.say(resources.getString(R.string.STORENAME_added_to_saved_places, storeName))
                    }
                }
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.storePlaceDetailsItemList()
    }

    override fun onCardClick(item: PlaceDetailsItem) {
        viewModel.say(item.storeName)
    }

    override fun onFavoriteClick(item: PlaceDetailsItem) {
        val index = placeDetailsItemList.indexOf(item)
        val favorite = viewModel.changeFavorite(item)
        adapter.notifyItemChanged(index)

        if (favorite) {
            viewModel.say(resources.getString(R.string.added_STORENAME_to_favorites, item.storeName))
        } else {
            viewModel.say(resources.getString(R.string.deleted_STORENAME_from_favorites, item.storeName))
        }
    }

    private fun onVoiceActivationButtonClick() {
        if (viewModel.isListening.value == false) {
            viewModel.handleSpeechBegin()
        } else {
            viewModel.cancelListening()
        }
    }

}