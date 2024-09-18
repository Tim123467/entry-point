# -*- coding: utf-8 -*-
"""
Program used to check what date a specified user has earned the Black Dawn badge.
"""
from urllib.request import urlopen
from urllib.error import URLError

while True:
    userID = -1
    
    while userID < 0:
        try:
            userID = input('Enter a valid Roblox user ID (or enter 0 to quit): ')
            userID = int(userID)
            
            if userID < 0:
                print('Invalid user ID.')
        except ValueError:
            print('Invalid user ID.')
            userID = -1
    
    if userID == 0:
        break
    
    isBlank = True
    
    try:
        with urlopen(f'https://badges.roblox.com/v1/users/{userID}/badges/2124422246/awarded-date') as response:
            for line in response:
                line = line.decode()
                isBlank = False
                print(f'User ID: {userID}')
                print(f'Date: {line[37:47]}')
                print(f'Time: {line[48:-3]} UTC')
        
        if isBlank:
            print(f'User {userID} either does not exist or does not have the Black Dawn badge.')
    except URLError:
        print('Either this user does not exist, or there was a problem running the program.')
