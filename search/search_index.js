var __index = {"config":{"lang":["en"],"separator":"[\\s\\-]+","pipeline":["stopWordFilter"]},"docs":[{"location":"index.html","title":"Overview","text":"<p>Welcome to the documentation of NLInterface Project!</p>"},{"location":"index.html#are-you-a-tester","title":"Are you a tester?","text":"<p>Checkout the installation guide to get the current version running on your Android device.</p>"},{"location":"index.html#are-you-a-developer","title":"Are you a developer?","text":"<p>Use our Android Studio Setup Guide and architecture to contribute new features and fix bugs.</p>"},{"location":"index.html#are-you-our-supervising-professor","title":"Are you our supervising professor?","text":"<p>We are not done yet, please look somewhere else.</p> <p></p>"},{"location":"architecture/overview.html","title":"How is our app structured?","text":"<pre><code>flowchart\n    subgraph activities\n    m[MainActivity]\n    m --&gt; GroceryListActivity\n    m --&gt; PlaceActivity\n    m --&gt; SettingsActivity\n\n    end\n    subgraph utilities\n    TextToSpeechUtility\n    SpeechToTextButton\n    SpeechToTextUtility\n\n    end</code></pre>"},{"location":"build/actions.html","title":"Github Actions","text":""},{"location":"build/actions.html#building-a-debug-apk","title":"Building a debug APK","text":""},{"location":"build/actions.html#creating-a-release","title":"Creating a release","text":""},{"location":"installation/android_studio_setup.html","title":"Android studio setup","text":"<p>t.b.d.</p>"},{"location":"installation/install_from_actions.html","title":"Install Debug Build from GitHub Actions","text":"<p>You can always install latest version of the main branch directly from GitHub Actions.</p> <p>Warning</p> <p>The latest build on the main branch is considered bleeding edge and will most certainly contain bugs! Only use it when you are testing and are fine with the app breaking.</p> <p>For more stabilized versions, use the one of the official releases.</p>"},{"location":"installation/install_from_actions.html#prerequisites","title":"Prerequisites","text":"<ul> <li>An Android device in developer mode</li> <li>You are logged into your GitHub account on your device</li> </ul> <p>Open up the repo on your device by following the link </p> <p>https://github.com/StudyProject-NLI/NLInterface</p> <p>and navigate to the Actions tab by pressing the three dots button on the right.</p> <p></p> <p>Select a successfull workflow run of Testing APK</p> <p></p> <p>Download the app-debug-build artifact.</p> <p></p>"},{"location":"installation/install_from_release.html","title":"Install from GitHub Release","text":"<p>t.b.d. when v0.0.1 is released.</p>"}]}