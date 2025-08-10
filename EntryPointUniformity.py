# -*- coding: utf-8 -*-
'''
Program that gives the uniformity of each mission.
The output is produced in the console.
'''

import numpy as np

with open('EP Daily Challenges Wikitext.txt') as fin:
    stealthMissions = ('blacksite', 'financier', 'deposit', 'lakehouse', 'withdrawal',
                       'scientist', 'scrs', 'killhouse', 'auction', 'gala', 'cache', 'setup', 'lockup')
    loudMissions = ('blacksite', 'financier', 'deposit', 'lakehouse', 'withdrawal', 'scientist',
                    'scrs', 'black dusk', 'killhouse', 'lockup', 'score')
    stealthModifiers = ('misplaced gear', 'boarded up', 'fog', 'no suppressors', 'no safecracking',
                        'weapon scanners', 'no scrambler', 'bloodless', 'unskilled', 'no hybrid classes',
                        'cascade arsenal', 'reinforced doors', 'heavy bags', 'no equipment bags',
                        'reinforced locks', 'no interrogation', 'extra cameras',
                        'unintimidating', 'hidden ui', 'faster detection', 'reinforced cameras',
                        'hidden detection bars', 'no lockpicks', 'inexperienced', 'fifteen minutes',
                        'takedown limit', 'no knockouts', 'no moving bodies', 'no disguise')
    loudModifiers = ('no aegis armor', 'boarded up', 'fog',
                     'unskilled', 'small arms only', 'criminal arsenal', 'no hybrid classes', 'cascade arsenal',
                     'flashbang frenzy', 'reinforced doors', 'armera arsenal', 'no equipment bags',
                     'reinforced locks', 'glass cannon', 'mandatory headshots', 'no heavy armor',
                     'weaker medkits', 'hidden ui', 'aegis academy', 'flashbang revenge',
                     'one shot', 'inexperienced', 'less health',
                     'no explosives', 'shield swarm', 'explosive flashbangs', 'explosive revenge')
    #Part 1: processing txt file
    stealthOccurrences = {mission: {modifier: 0 for modifier in stealthModifiers} for mission in stealthMissions}
    loudOccurrences = {mission: {modifier: 0 for modifier in loudModifiers} for mission in loudMissions}
    stealthMissionOccurrences = {mission: 0 for mission in stealthMissions}
    loudMissionOccurrences = {mission: 0 for mission in loudMissions}

    strings = ['','','','',''] #represents mission, tactic, and each modifier respectively
    is2022 = False

    #processes the txt file
    while True:
        strings[0] = fin.readline()
        if strings[0] == '': #stops the loop when there's no more lines to read
            break
        
        if strings[0].startswith('|The') or strings[0].startswith('|Black') or strings[0].startswith('|{{Robux'):
            strings[1] = fin.readline().lower()
            strings[2] = fin.readline().strip().lower()
                
            strings[0] = strings[0][strings[0].index('e') + 1:].strip().lower() if 'The' in strings[0] else 'black dusk'
            strings[4] = strings[2][strings[2].rindex('>', 0, -2) + 1 : strings[2].rindex('<')].strip()
            strings[2] = strings[2][:strings[2].rindex(',', 0, -2)]
            strings[3] = strings[2][strings[2].rindex('>', 0, -2) + 1 : strings[2].rindex('<')].strip()
            strings[2] = strings[2][strings[2].index('>') + 1 : strings[2].index('<', 2)].strip()
                
            strings[2] = strings[2][:strings[2].index('(')].strip() if strings[2].endswith(')') else strings[2]
            strings[3] = strings[3][:strings[3].index('(')].strip() if strings[3].endswith(')') else strings[3]
            strings[4] = strings[4][:strings[4].index('(')].strip() if strings[4].endswith(')') else strings[4]
                
            #increments the values in the dictionaries
            if is2022:
                if 'stealth' in strings[1]:
                    stealthMissionOccurrences[strings[0]] += 1
                    stealthOccurrences[strings[0]][strings[2]] += 1
                    stealthOccurrences[strings[0]][strings[3]] += 1
                    stealthOccurrences[strings[0]][strings[4]] += 1
                elif 'loud' in strings[1]:
                    loudMissionOccurrences[strings[0]] += 1
                    loudOccurrences[strings[0]][strings[2]] += 1
                    loudOccurrences[strings[0]][strings[3]] += 1
                    loudOccurrences[strings[0]][strings[4]] += 1
                else:
                    raise ValueError('The part in the txt file corresponding with a tactic in the 2022 or later daily challenges was invalid. The daily challenge entry with the invalid tactic is listed below:\n'
                                     + (string + '\n') for string in strings)
        elif strings[0].strip() == '===January 2022===':
            is2022 = True
    
    #Part 2: uniformity calculations
    for mission in stealthMissions:
        gini = 0
        entropy = 0
        error = 0
        for modifier in stealthModifiers:
            stealthFreq = stealthOccurrences[mission][modifier] / stealthMissionOccurrences[mission] / 3
            gini += stealthFreq ** 2
            entropy += 0 if stealthFreq == 0 else stealthFreq * np.log2(stealthFreq)
            error = np.maximum(stealthFreq, error)
        gini = 1 - gini
        entropy *= -1
        error = 1 - error
        print(f'{mission} stealth has gini = {gini}, entropy = {entropy}, and error = {error}.')
    print()
    for mission in loudMissions:
        gini = 0
        entropy = 0
        error = 0
        for modifier in loudModifiers:
            loudFreq = loudOccurrences[mission][modifier] / loudMissionOccurrences[mission] / 3
            gini += loudFreq ** 2
            entropy += 0 if loudFreq == 0 else loudFreq * np.log2(loudFreq)
            error = np.maximum(loudFreq, error)
        gini = 1 - gini
        entropy *= -1
        error = 1 - error
        print(f'{mission} loud has gini = {gini}, entropy = {entropy}, and error = {error}.')
