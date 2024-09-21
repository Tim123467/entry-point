# -*- coding: utf-8 -*-
'''
Program that gives the probability of each modifier appearing on a certain mission.
The output is produced in a separate text file.
'''

with open('EP Daily Challenges Wikitext.txt') as fin:
    with open('EP Probabilities Generated Output.txt', 'w') as fout:
        stealthMissions = ('blacksite', 'financier', 'deposit', 'lakehouse', 'withdrawal',
        'scientist', 'scrs', 'killhouse', 'auction', 'gala', 'cache', 'setup', 'lockup')
        stealthMissionsNoExpansions = ('blacksite', 'financier', 'deposit', 'lakehouse', 'withdrawal',
        'scientist', 'scrs', 'killhouse')
        loudMissions = ('blacksite', 'financier', 'deposit', 'lakehouse', 'withdrawal', 'scientist',
        'scrs', 'black dusk', 'killhouse', 'lockup', 'score')
        loudMissionsNoExpansions = ('blacksite', 'financier', 'deposit', 'lakehouse', 'withdrawal',
        'scientist', 'scrs', 'black dusk', 'killhouse')
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
        
        stealth2021 = {mission: {modifier: 0 for modifier in stealthModifiers} for mission in stealthMissions}
        loud2021 = {mission: {modifier: 0 for modifier in loudModifiers} for mission in loudMissions}
        stealth2022 = {mission: {modifier: 0 for modifier in stealthModifiers} for mission in stealthMissions}
        loud2022 = {mission: {modifier: 0 for modifier in loudModifiers} for mission in loudMissions}
        stealth2021missions = {mission: 0 for mission in stealthMissions}
        loud2021missions = {mission: 0 for mission in loudMissions}
        stealth2022missions = {mission: 0 for mission in stealthMissions}
        loud2022missions = {mission: 0 for mission in loudMissions}
        
        strings = ['','','','',''] #represents mission, tactic, and each modifier respectively
        is2021 = True
        
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
                if is2021:
                    if 'stealth' in strings[1] and strings[0] != 'black dusk':
                        stealth2021missions[strings[0]] += 1
                        stealth2021[strings[0]][strings[2]] += 1
                        stealth2021[strings[0]][strings[3]] += 1
                        stealth2021[strings[0]][strings[4]] += 1
                    elif 'loud' in strings[1]:
                        loud2021missions[strings[0]] += 1
                        loud2021[strings[0]][strings[2]] += 1
                        loud2021[strings[0]][strings[3]] += 1
                        loud2021[strings[0]][strings[4]] += 1
                    elif not (strings[0] == 'black dusk' and 'stealth' in strings[1]):
                        raise ValueError('The part in the txt file corresponding with a tactic in the 2021 daily challenges was invalid.')
                else: #not 2021
                    if 'stealth' in strings[1]:
                        stealth2022missions[strings[0]] += 1
                        stealth2022[strings[0]][strings[2]] += 1
                        stealth2022[strings[0]][strings[3]] += 1
                        stealth2022[strings[0]][strings[4]] += 1
                    elif 'loud' in strings[1]:
                        loud2022missions[strings[0]] += 1
                        loud2022[strings[0]][strings[2]] += 1
                        loud2022[strings[0]][strings[3]] += 1
                        loud2022[strings[0]][strings[4]] += 1
                    else:
                        raise ValueError('The part in the txt file corresponding with a tactic in the 2022 or later daily challenges was invalid.')
            elif strings[0].strip() == '===January 2022===':
                is2021 = False
        
        #displays how often modifiers appear as decimals
        fout.write('Stealth:\n')
        for modifier in stealthModifiers:
            fout.write(f'{modifier}:\n')
            for mission in stealthMissions:
                fout.write(f'{mission}: {stealth2022[mission][modifier] / stealth2022missions[mission]}\n')
            fout.write('\n')
        
        fout.write('Loud:\n')
        for modifier in loudModifiers:
            fout.write(f'{modifier}:\n')
            for mission in loudMissions:
                fout.write(f'{mission}: {loud2022[mission][modifier] / loud2022missions[mission]}\n')
            fout.write('\n')
        
        #displays which modifiers were removed from certain missions from 2021 to 2022
        for modifier in stealthModifiers:
            if modifier != 'boarded up' and modifier != 'no equipment bags': #these modifiers were removed from the stealth pool
                for mission in stealthMissionsNoExpansions:
                    if stealth2021[mission][modifier] != 0 and stealth2022[mission][modifier] == 0:
                        fout.write(f'{modifier} was removed from {mission}\n')
        
        for modifier in loudModifiers:
            for mission in loudMissionsNoExpansions:
                if loud2021[mission][modifier] != 0 and loud2022[mission][modifier] == 0:
                    fout.write(f'{modifier} was removed from {mission}\n')
        
        print('Script has been executed.')
