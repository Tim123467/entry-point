# -*- coding: utf-8 -*-
'''
Program to verify whether a duplicate mission can occur in between Night Heists.
The output is produced in the console.
'''

class DuplicateMission(Exception):
    def __init__(self, message):
        self.message = message

with open('EP Daily Challenges Wikitext.txt') as fin:
    freeMissions = ('blacksite', 'financier', 'deposit', 'lakehouse', 'withdrawal', 'scientist',
        'scrs', 'black dusk', 'killhouse')
    freelanceHeists = ('setup', 'lockup', 'score')

    numOccurrences = {mission: 0 for mission in freeMissions}

    try:
        #processes the txt file
        while True:
            line = fin.readline().strip()
            if line == '': #stops the loop when there's no more lines to read
                break
            
            for mission in freeMissions:
                if mission in line.lower():
                    numOccurrences[mission] += 1
                    if numOccurrences[mission] > 1:
                        raise DuplicateMission(f'Duplicate detected. Mission: {mission}')
                    break
            for mission in freelanceHeists:
                if mission in line.lower():
                    numOccurrences = {mission: 0 for mission in freeMissions}
                    break
    except DuplicateMission as e:
        print(e.message)
    else:
        print('No duplicates detected.')
