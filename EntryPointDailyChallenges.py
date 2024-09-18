# -*- coding: utf-8 -*-
'''
Program used to translate Breakbar's txt file into wikitext. Note that I renamed the txt file to "EP Daily Challenges.txt"
The only edits that should be made are to the values of the variables near the top: txtFileName, currentYearAndMonth, and lastYear.
(unless you want to try to improve the code)
The output is produced in a separate text file.
'''

txtFileName = 'EP Daily Challenges.txt' #change depending on the name you set for Breakbar's txt file
currentYearAndMonth = '2024-09' #must be in the format "YYYY-MM"
lastYear = 2025 #the last year that the txt file has daily challenges for, as an integer

while True: #prompts for year
    try:
        year = input('Enter the year you want the wikitext for: ')
        if 2022 <= int(year) <= lastYear:
            break
        else:
            print(f'The txt file only has daily challenges for years starting at 2022 and ending at {lastYear}.')
    except ValueError:
        print('Invalid input.')

with open(txtFileName) as fin:
    with open('EP Daily Challenges Generated Wikitext.txt', 'w') as fout:
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
                        fout.write(f"'''Daily Challenges''' from {year} will be displayed here.\n\n===January")
                    else:
                        fout.write('|}\n===')
                        if date[5:7] == '02':
                            fout.write('February')
                        elif date[5:7] == '03':
                            fout.write('March')
                        elif date[5:7] == '04':
                            fout.write('April')
                        elif date[5:7] == '05':
                            fout.write('May')
                        elif date[5:7] == '06':
                            fout.write('June')
                        elif date[5:7] == '07':
                            fout.write('July')
                        elif date[5:7] == '08':
                            fout.write('August')
                        elif date[5:7] == '09':
                            fout.write('September')
                        elif date[5:7] == '10':
                            fout.write('October')
                        elif date[5:7] == '11':
                            fout.write('November')
                        elif date[5:7] == '12':
                            fout.write('December')
                        else:
                            print('\nThe part in the txt file corresponding with the month was invalid.')
                            raise ValueError
                    fout.write(f' {year}===\n{{| class="fandom-table article-table mw-collapsible mw-collapsed" ')
                    
                    if date[:7] == currentYearAndMonth: #current year and current month
                        fout.write('data-expandtext="Show Previous and Upcoming Challenges" data-collapsetext="Hide Previous and Upcoming Challenges"\n')
                    elif int(year) < int(currentYearAndMonth[:4]) or (int(year) == int(currentYearAndMonth[:4]) and int(date[5:7]) < int(currentYearAndMonth[5:7])):
                        #earlier year OR same year but earlier month
                        fout.write('data-expandtext="Show Previous Challenges" data-collapsetext="Hide Previous Challenges"\n')
                    else: #current year and later month OR later year
                        fout.write('data-expandtext="Show Upcoming Challenges" data-collapsetext="Hide Upcoming Challenges"\n')
                    fout.write('!Date\n!Mission\n!Tactic\n!Modifiers\n')
                
                fout.write('|-\n!' + date + '\n|')
                
                if mission == 'The Scientist' and mod1 == 'No Suppressors' and mod2 == 'Hidden UI' and mod3 == 'No Knockouts': #invalid modifier combinations
                    fout.write('The Scientist\n|Stealth\n|<span class=challenge-green>Fog</span>, <span class=challenge-green>No Suppressors</span>, <span class=challenge-purple>Hidden UI</span>\n')
                elif mission == 'The Scientist' and (mod1 == 'No Suppressors' or mod2 == 'No Suppressors') and mod3 == 'No Knockouts':
                    fout.write(f'The Scientist\n|Stealth\n|<span class=challenge-green>{mod1}</span>, <span class=challenge-')
                    
                    if color2 == 'G':
                        fout.write('green')
                    elif color2 == 'B':
                        fout.write('blue')
                    elif color2 == 'P':
                        fout.write('purple')
                    elif color2 == 'R':
                        fout.write('red')
                    else:
                        print("\nThe part in the txt file corresponding with the second modifier's color was invalid.")
                        raise ValueError
                    
                    fout.write(f'>{mod2}</span>, <span class=challenge-purple>Hidden UI</span>\n')
                else:
                    if mission == 'The Auction' or mission == 'The Gala' or mission == 'The Cache' or mission == 'The Setup' or mission == 'The Lockup' or mission == 'The Score':
                        fout.write('{{Robux}} ')
                    
                    fout.write(f'{mission}\n|{tactic}\n|<span class=challenge-')
                    
                    if color1 == 'G':
                        fout.write('green')
                    elif color1 == 'B':
                        fout.write('blue')
                    elif color1 == 'P':
                        fout.write('purple')
                    elif color1 == 'R':
                        fout.write('red')
                    else:
                        print("\nThe part in the txt file corresponding with the first modifier's color was invalid.")
                        raise ValueError
                    
                    fout.write(f'>{mod1}</span>, <span class=challenge-')
                    
                    if color2 == 'G':
                        fout.write('green')
                    elif color2 == 'B':
                        fout.write('blue')
                    elif color2 == 'P':
                        fout.write('purple')
                    elif color2 == 'R':
                        fout.write('red')
                    else:
                        print("\nThe part in the txt file corresponding with the second modifier's color was invalid.")
                        raise ValueError
                    
                    fout.write(f'>{mod2}</span>, <span class=challenge-')
                    
                    if color3 == 'G':
                        fout.write('green')
                    elif color3 == 'B':
                        fout.write('blue')
                    elif color3 == 'P':
                        fout.write('purple')
                    elif color3 == 'R':
                        fout.write('red')
                    else:
                        print("\nThe part in the txt file corresponding with the third modifier's color was invalid.")
                        raise ValueError
                    
                    fout.write(f'>{mod3}</span>\n')
        
        fout.write('|}')
        print('Script has been executed.')
