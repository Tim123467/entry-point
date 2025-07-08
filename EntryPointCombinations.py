#!/usr/bin/env python3
# -*- coding: utf-8 -*-
'''
Program that calculates and outputs the total number of possible combinations of daily challenges.
The output is produced in the console.
'''

import math

with open('EP Probabilities Generated Output.txt') as fin: #change depending on the name you set for the output of EntryPointProbabilities.py
    stealthMissions = ('blacksite', 'financier', 'deposit', 'lakehouse', 'withdrawal',
    'scientist', 'scrs', 'killhouse', 'auction', 'gala', 'cache', 'setup', 'lockup')
    loudMissions = ('blacksite', 'financier', 'deposit', 'lakehouse', 'withdrawal', 'scientist',
    'scrs', 'black dusk', 'killhouse', 'lockup', 'score')
    
    stealthNumModifiers = {mission: 0 for mission in stealthMissions}
    loudNumModifiers = {mission: 0 for mission in loudMissions}
    
    isStealth = True
    
    #processes the txt file
    while True:
        line = fin.readline().strip()
        if 'was' in line: #the last part of the txt file is not needed
            break
        if line == 'Loud:':
            isStealth = False
            continue
        if line.endswith('0.0') or '.' not in line:
            continue
        
        if isStealth:
            stealthNumModifiers[line[0:line.index(':')]] += 1
        else: #is loud
            loudNumModifiers[line[0:line.index(':')]] += 1
    
    #calculate raw num combos for each mission
    stealthNumCombos = {mission: math.comb(stealthNumModifiers[mission], 3) for mission in stealthMissions}
    loudNumCombos = {mission: math.comb(loudNumModifiers[mission], 3) for mission in loudMissions}
    
    #remove No Knockouts & No Suppressors combos
    stealthNumCombos['scientist'] -= stealthNumModifiers['scientist'] - 2
    
    #displays the number of combinations
    totalStealth = 0
    totalLoud = 0
    print('Stealth:')
    for mission in stealthMissions:
        totalStealth += stealthNumCombos[mission]
        print(f'{mission}: {stealthNumCombos[mission]}')
    print(f'Total stealth: {totalStealth}')
    print('\nLoud:')
    for mission in loudMissions:
        totalLoud += loudNumCombos[mission]
        print(f'{mission}: {loudNumCombos[mission]}')
    print(f'Total loud: {totalLoud}')
    print(f'\nGrand total: {totalStealth + totalLoud}')
