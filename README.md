# Black Dawn date checker (no downloads required version)
Requires a Google account (if you have a YouTube channel then you have a Google account). Your Google account can stay private, you do not need to share it.
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

# Daily Challenges programs
There are three different programs, two of which are available in both Java and Python. The output for the Java versions is produced in the console, while the output for the Python versions is produced in a separate text file.
1) A program that translates Breakbar's txt file into wikitext.

    1.1. Java: "EntryPointDailyChallenges.java". Edit the values of the 3 final static String variables at the top.

    1.2. Python: "EntryPointDailyChallenges.py". Edit the values of the 3 variables at the top then enter the year you want the wikitext for in the console.

2) A program that gives the probability of each modifier appearing on a certain mission and gives a list of modifiers removed from certain missions. The Java version has two different implementations: one using enums and EnumMaps ("EntryPointProbabilities.java") and one that avoids enums and uses HashMaps instead of EnumMaps ("EntryPointProbabilitiesNoEnums.java"). The Python version is "EntryPointProbabilities.py".

3) A program that translates the output of EntryPointProbabilities.java into wikitext. It is "EntryPointProbabilitiesWikitext.java". I currently do not plan on making a Python version of it.
