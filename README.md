<h1> The Whitworthian -- Android Application -- Source Files </h1>

<h2>HOW TO PUT ONTO SCHOOL COMPUTER:</h2>
____________________________
<ol>
<li> Create New Project </li>
<li> App Name: whitworthian_v2 </li>
<li> Module Name: app </li>
<li> Package Name: mayhem.whitworthian_v2.app </li>
<li> Min API: 14 </li>
<li> Compile with: 19 </li>
<li> Language level: 6.0 </li>
<li> Theme: Holo Light with Dark Action Bar </li>
<li> Leave check boxes alone (yes - create custom launcher icon, create activity.  no - mark this project as a library) </li>
<li> Click next 3x </li>
<li> Rename activity SplashActivity
<li> Click Finish
</ol>
_______________________________________________________
<ol>
<li><p>Ensure that the following activities are created:</p>
<ul>
<li>SplashActivity
<li>ArticleListActivity
<li>ArticleViewActivity
<li>GenreListActivity
<li>SearchResultsActivity
</ul>
</li>
<li><p>Create the following java classes:</p>

<ul>
<li>RssHandler</li>
<li>ArticleSelection </li>
<li>ArticleSelectionAdapter </li>
<li>Article </li>
</ul>

</li>

<li><p>Create the following layouts:</p>

<ul>
<li>article_list_item_row </li>
</ul>
</li>

<li><p>Create the following directory under "res" directory: xml.  Add the following files to that directory</p>

<ul>
<li>searchable.xml </li>
</ul>
</li>
</ol>
_____________________________________________________________________________________
<ol>
<li> git init just inside of whitworthian_v2 folder(where you see app, build.gradle, etc.) </li>
<li> git remote add origin https://github.com/ptucker/whitworthian_Android_App.git </li>
<li> git fetch --all </li>
<li> git reset --hard origin/master </li>
</ol>
_______________________________________________
<ol>
<li> Make sure you have android support repository installed in sdk manager (using C:\Android-SDK-Eclipse\SDK Manager) </li>
<li> file-> project structure -> Android SDK in platform settings -> android sdk location: C:\Android-SDK-Eclipse\sdk </li>
<li> <p> Ensure the following is in your dependencies block (at the bottom of build.gradle) </p>


<p>dependencies { </p>
<p>    compile 'com.android.support:support-v4:+' </p>
<p>    compile 'com.android.support:appcompat-v7:+' </p>
<p>}</p>

</li>

<li> Build and run! </li>
</ol>
_______________________________________________

<h2>Steps to update files in GitHub</h2>
_______________________________________________
<ol>
<li>git status</li>
<li>git add *file* </li>
<li>git commit -m "Message" </li>
<li>git push origin master</li>
</ol>



______________________________________
<p> To get files back down from GitHub, either follow the steps above in creation, or: </p>
<p> git fetch --all </p>
<p> git pull origin master </p>
