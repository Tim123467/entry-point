# Black Dawn date checker (no downloads required version)
This version requires you to have a Google account (if you have a YouTube channel then you have a Google account). Your Google account can stay private, you do not need to share it.

There are 2 different methods to access this version.

Method 1:
1) Go to https://colab.research.google.com/github/Tim123467/entry-point/blob/main/EntryPointBlackDawnDateChecker.ipynb
2) You can run the code from that website.

Method 2:
1) Open "EntryPointBlackDawnDateChecker.ipynb"
2) Copy the part in the box next to "In [13]:". The part you should copy starts with "# -\*- coding: utf-8 -\*-" and ends with "print('Time: ' + r.text[48:-3] + ' UTC')".
3) Go to https://colab.research.google.com/
4) Click "New notebook"
5) Paste the code into the box.
6) Run the code. To access the program again, go to your Google Drive and there should be a folder called "Colab Notebooks" containing this program.

# Black Dawn date checker (original versions)
Both versions of this program are known to bug out on online IDEs, so instead I recommend downloading and using either Eclipse (https://eclipseide.org/), Anaconda (https://www.anaconda.com/download/success), or VS Code (https://code.visualstudio.com/docs/setup/setup-overview).

1) Java: "EntryPointBlackDawnDateChecker.java". I recommend using Eclipse to run this.
2) Python: "EntryPointBlackDawnDateChecker.py". I recommend opening Anaconda Navigator (included in the Anaconda Distribution) and using Spyder to run this.
3) If you have VS Code installed, it can be used as an alternative to Eclipse/Anaconda. I tried both versions in VS Code and the Python version ran without issues. Due to my lack of experience working with Java in VS Code, I could not figure out how to get any Java program to run in VS Code. Since the Python version worked in VS Code, if you can get Java programs to run in VS Code then I assume the Java version will work too (although this is just an assumption).

# Daily challenges programs
Tip: if you're using Google Colab, don't download the .py file. Instead, copy the code and paste it into a new Google Colaboratory.

There are 4 different programs, two of which are available in both Java and Python. The output for the Java versions is produced in the console, while the output for the Python versions is produced in a separate text file (unless the file extension is .ipynb instead of.py).
1) A program that translates Breakbar's txt file into wikitext.

    1.1. Java: "EntryPointDailyChallenges.java". Edit the values of the 3 final static String variables at the top.

    1.2. Python: "EntryPointDailyChallenges.ipynb". Edit the values of the 3 variables at the top. Similarly to the Black Dawn Date Checker, it can also be accessed at https://colab.research.google.com/github/Tim123467/entry-point/blob/main/EntryPointDailyChallenges.ipynb

2) A program that gives the probability of each modifier appearing on a certain mission and gives a list of modifiers removed from certain missions. The Java version has two different implementations: one using enums and EnumMaps ("EntryPointProbabilities.java") and one that avoids enums and uses HashMaps instead of EnumMaps ("EntryPointProbabilitiesNoEnums.java"). The Python version is "EntryPointProbabilities.py".

The other programs listed below currently do not have Python versions.

3) A program that translates the output of EntryPointProbabilities.java into wikitext. It is "EntryPointProbabilitiesWikitext.java".

4) A program that gives the uniformity of each mission and tactic.

5) A program that outputs the number of combinations of daily challenge modifiers.
