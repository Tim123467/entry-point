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
Tip: if you're using Google Colab, don't download the .py file. Instead, copy the code and paste it into a Google Colaboratory code block and run it.

There are 6 different programs, 5 of which are available in both Java and Python. The output for the Java versions is produced in the console, while the output for the Python versions is produced either in a separate text file or in the console.
1) A program that translates Breakbar's txt file into wikitext.

    1.1. Java: "EntryPointDailyChallenges.java". Edit the values of the 3 final static String variables at the top.

    1.2. Python: "EntryPointDailyChallenges.ipynb". Edit the values of the 3 variables at the top. Similarly to the Black Dawn Date Checker, it can also be accessed at https://colab.research.google.com/github/Tim123467/entry-point/blob/main/EntryPointDailyChallenges.ipynb (the output of the .ipynb will be produced in the output cell under the code cell).

2) A program that gives the probability of each modifier appearing on a certain mission and gives a list of modifiers removed from certain missions. The Java version has two different implementations: one using enums and EnumMaps ("EntryPointProbabilities.java") and one that avoids enums and uses HashMaps instead of EnumMaps ("EntryPointProbabilitiesNoEnums.java"). The Python version is "EntryPointProbabilities.py" and will generate a text file for its output.
3) A program that gives the number of combinations of daily challenges for each tactic and mission. The total numbers for each tactic and the grand total are also outputted. The Java version is "EntryPointCombinations.java" and the Python version is "EntryPointCombinations.py". Both versions will produce outputs in the console instead of generating a separate text file.
4) A program that gives the uniformity of each mission and tactic. The Java version is "EntryPointUniformity.java" and the Python version is "EntryPointUniformity.py". Both versions will produce outputs in the console instead of generating a separate text file. If you imagine each combination of missions and tactics (ex. The Blacksite stealth) having their own barchart listing every possible modifier and their frequency, the uniformity is a measure of how close to uniform that barchart is.
5) A program used to verify that the statement "There cannot be two of the same mission in between every Freelance Heist challenge." cannot be made any more specific. The Java version is "EntryPointDuplicateMissions.java" and the Python version is "EntryPointDuplicateMissions.py". Both versions will produce outputs in the console instead of generating a separate text file.

The last program currently does not have a Python version.

6) A program that translates the output of EntryPointProbabilities.java into wikitext. It is "EntryPointProbabilitiesWikitext.java".
