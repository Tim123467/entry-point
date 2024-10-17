'''
Program used to translate Breakbar's txt file into wikitext. Note that I renamed the txt file to "EP Daily Challenges.txt"
The only edits that should be made are to the values of the variables near the top: txtFileName, currentYearAndMonth, and lastYear.
(unless you want to try to improve the code)
The output is produced in the console (which should allow enough lines of output to be displayed, confirmed on Google Colab).
'''
import urllib

fileName = 'EP Daily Challenges.txt' #change depending on the name you want for Breakbar's txt file

urllib.request.urlretrieve('https://github.com/The-breakbar/Entry-Point-Wiki-Bot/raw/main/utils/DailyChallengeList.txt', fileName)

currentYearAndMonth = '2024-10' #must be in the format "YYYY-MM"
year = '2024' #change manually for the year you want the wikitext for
#currently, the txt file covers the end of 2022 to most of 2025

with open(fileName) as fin:
    while True:
        line = fin.readline()
        if line == '': #stops the loop when there's no more lines to read
            break
        date = line[:line.index(',')]
            
        if date[:4] == year: #checks if the year matches the specified year
            line = line[line.index(',') + 1:]
            mission = line[:line.index(',')]
            line = line[line.index(',') + 1:]
            tactic = line[:line.index(',')]
            line = line[line.index(',') + 1:]
            color1 = line[:line.index(',')]
            line = line[line.index(',') + 1:]
            mod1 = line[:line.index(',')]
            mod1 = 'Takedown Limit (4)' if mod1 == 'Takedown Limit' else 'Takedown Limit (6)' if mod1 == 'Takedown Limit 6' else mod1
            line = line[line.index(',') + 1:]
            color2 = line[:line.index(',')]
            line = line[line.index(',') + 1:]
            mod2 = line[:line.index(',')]
            mod2 = 'Takedown Limit (4)' if mod2 == 'Takedown Limit' else 'Takedown Limit (6)' if mod2 == 'Takedown Limit 6' else mod2
            line = line[line.index(',') + 1:]
            color3 = line[:line.index(',')]
            mod3 = line[line.index(',') + 1:].strip()
            mod3 = 'Takedown Limit (4)' if mod3 == 'Takedown Limit' else 'Takedown Limit (6)' if mod3 == 'Takedown Limit 6' else mod3
                
            if date[8:10] == '01': #first day of the month
                if date[5:7] == '01': #first month of the year
                    print(f"'''Daily Challenges''' from {year} will be displayed here.\n\n===January",end='')
                else:
                    print('|}\n===',end='')
                    if date[5:7] == '02':
                        print('February',end='')
                    elif date[5:7] == '03':
                        print('March',end='')
                    elif date[5:7] == '04':
                        print('April',end='')
                    elif date[5:7] == '05':
                        print('May',end='')
                    elif date[5:7] == '06':
                        print('June',end='')
                    elif date[5:7] == '07':
                        print('July',end='')
                    elif date[5:7] == '08':
                        print('August',end='')
                    elif date[5:7] == '09':
                        print('September',end='')
                    elif date[5:7] == '10':
                        print('October',end='')
                    elif date[5:7] == '11':
                        print('November',end='')
                    elif date[5:7] == '12':
                        print('December',end='')
                    else:
                        raise ValueError('The part in the txt file corresponding with the month was invalid.')
                print(f' {year}===\n{{| class="fandom-table article-table mw-collapsible mw-collapsed" ',end='')
                    
                if date[:7] == currentYearAndMonth: #current year and current month
                    print('data-expandtext="Show Previous and Upcoming Challenges" data-collapsetext="Hide Previous and Upcoming Challenges"')
                elif int(year) < int(currentYearAndMonth[:4]) or (int(year) == int(currentYearAndMonth[:4]) and int(date[5:7]) < int(currentYearAndMonth[5:7])):
                    #earlier year OR same year but earlier month
                    print('data-expandtext="Show Previous Challenges" data-collapsetext="Hide Previous Challenges"')
                else: #current year and later month OR later year
                    print('data-expandtext="Show Upcoming Challenges" data-collapsetext="Hide Upcoming Challenges"')
                print('!Date\n!Mission\n!Tactic\n!Modifiers')
                
            print('|-\n!' + date + '\n|',end='')
                
            if mission == 'The Scientist' and mod1 == 'No Suppressors' and mod2 == 'Hidden UI' and mod3 == 'No Knockouts': #invalid modifier combinations
                print('The Scientist\n|Stealth\n|<span class=challenge-green>Fog</span>, <span class=challenge-green>No Suppressors</span>, <span class=challenge-purple>Hidden UI</span>')
            elif mission == 'The Scientist' and (mod1 == 'No Suppressors' or mod2 == 'No Suppressors') and mod3 == 'No Knockouts':
                if color2 == 'G':
                    print(f'The Scientist\n|Stealth\n|<span class=challenge-green>{mod1}</span>, <span class=challenge-green>{mod2}</span>, <span class=challenge-purple>Hidden UI</span>')
                elif color2 == 'B':
                    print(f'The Scientist\n|Stealth\n|<span class=challenge-green>{mod1}</span>, <span class=challenge-blue>{mod2}</span>, <span class=challenge-purple>Hidden UI</span>')
                elif color2 == 'P':
                    print(f'The Scientist\n|Stealth\n|<span class=challenge-green>{mod1}</span>, <span class=challenge-purple>{mod2}</span>, <span class=challenge-purple>Hidden UI</span>')
                else:
                    raise ValueError("The part in the txt file corresponding with the second modifier's color was invalid.")
            else:
                if mission == 'The Auction' or mission == 'The Gala' or mission == 'The Cache' or mission == 'The Setup' or mission == 'The Lockup' or mission == 'The Score':
                    print('{{Robux}} ',end='')
                    
                if color1 == 'G':
                    print(f'{mission}\n|{tactic}\n|<span class=challenge-green>{mod1}</span>, ',end='')
                elif color1 == 'B':
                    print(f'{mission}\n|{tactic}\n|<span class=challenge-blue>{mod1}</span>, ',end='')
                elif color1 == 'P':
                    print(f'{mission}\n|{tactic}\n|<span class=challenge-purple>{mod1}</span>, ',end='')
                elif color1 == 'R':
                    print(f'{mission}\n|{tactic}\n|<span class=challenge-red>{mod1}</span>, ',end='')
                else:
                    raise ValueError("The part in the txt file corresponding with the first modifier's color was invalid.")
                    
                print(f'',end='')
                    
                if color2 == 'G':
                    print(f'<span class=challenge-green>{mod2}</span>, ',end='')
                elif color2 == 'B':
                    print(f'<span class=challenge-blue>{mod2}</span>, ',end='')
                elif color2 == 'P':
                    print(f'<span class=challenge-purple>{mod2}</span>, ',end='')
                elif color2 == 'R':
                    print(f'<span class=challenge-red>{mod2}</span>, ',end='')
                else:
                    raise ValueError("The part in the txt file corresponding with the second modifier's color was invalid.")
                    
                if color3 == 'G':
                    print(f'<span class=challenge-green>{mod3}</span>')
                elif color3 == 'B':
                    print(f'<span class=challenge-blue>{mod3}</span>')
                elif color3 == 'P':
                    print(f'<span class=challenge-purple>{mod3}</span>')
                elif color3 == 'R':
                    print(f'<span class=challenge-red>{mod3}</span>')
                else:
                    raise ValueError("The part in the txt file corresponding with the third modifier's color was invalid.")
    print('|}')
