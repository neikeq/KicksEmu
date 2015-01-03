"""
Retrieves information about skills from a memory dump
and creates a table with it
"""

def bytes_to_short(data, index=0):
    return (data[index+1] << 8) + data[index]


def bytes_to_int(data, index=0):
    return (data[index+3] << 24) + (data[index+2] << 16) + \
           (data[index+1] << 8) + data[index]


def read_bytes(path):
    return open(path, 'rb').read()


def retrieve_data(name, index):
    list = [
        name, bytes_to_int(dump, index - 94),
        bytes_to_short(dump, index - 90), bytes_to_short(dump, index - 86),
        bytes_to_int(dump, index - 82), bytes_to_int(dump, index - 78),
        bytes_to_int(dump, index - 74), bytes_to_int(dump, index - 62),
        bytes_to_int(dump, index - 58), bytes_to_int(dump, index - 54)
    ]

    print(','.join(str(x) for x in list))

skills = [
    'Skill_AxeKick', 'Skill_BackDribble', 'Skill_BackPass',
    'Skill_BeckhamCross', 'Skill_BeckhamShoot', 'Skill_BodyCatch',
    'Skill_BodyCheck', 'Skill_CarlosShoot', 'Skill_CenterHeading',
    'Skill_ChoicePoint', 'Skill_CounterTackle', 'Skill_CruijfTurn',
    'Skill_DirectThruPass', 'Skill_DirectTurnKick', 'Skill_DivingBlock',
    'Skill_DreadTackle', 'Skill_EdmilsonKick', 'Skill_Endure',
    'Skill_FakeBeckhamCross', 'Skill_FakeCross', 'Skill_FakeCross2',
    'Skill_FakeDribble', 'Skill_FakePass', 'Skill_FakeTurnSide',
    'Skill_FastCross', 'Skill_FastDribble', 'Skill_FastScreen',
    'Skill_FastSprint', 'Skill_FastTackle', 'Skill_FlipFlap',
    'Skill_FlyingShoot', 'Skill_GhostCross', 'Skill_GhostCross2',
    'Skill_HighClear', 'Skill_HighCross', 'Skill_HighTrapping',
    'Skill_HitAndRun', 'Skill_InsideKick', 'Skill_InstepKick',
    'Skill_IronWall', 'Skill_KillThrupass', 'Skill_LavonaKick',
    'Skill_LightningKick', 'Skill_LightningTackle', 'Skill_Lobbingpass',
    'Skill_LoseTimeShoot', 'Skill_LowCross', 'Skill_MarseilleRoulette',
    'Skill_MaxClear', 'Skill_MaxTrapping', 'Skill_MexicanJump',
    'Skill_MiddleSpinKick', 'Skill_MountainBlock', 'Skill_MountineBlock',
    'Skill_OverHeadKick', 'Skill_PowerfulShoot', 'Skill_Provocation',
    'Skill_PushOut', 'Skill_QuickSteal', 'Skill_QuickTackle',
    'Skill_RapidSteal', 'Skill_ScissorsKick', 'Skill_ShortStepOver',
    'Skill_SideStep', 'Skill_SideStep2', 'Skill_SlipDownKick',
    'Skill_StealCatch', 'Skill_StepOver', 'Skill_SummerSoltKick',
    'Skill_TackleAndCatch', 'Skill_TackleCatch', 'Skill_TargetCross',
    'Skill_TargetKillpass', 'Skill_TargetShortpass', 'Skill_TurnRoundKick',
    'Skill_TurnSideKick', 'Skill_TurningShoot'
]

dump = read_bytes('skills.dump')

print('Name,Id,Position,Level,Kash 7,Kash 30,Kash Perm,'
      'Points 7,Points 30,Points Perm\n,')

missing = []

for (name) in skills:
    pattern = bytes(name, 'utf-8')
    index = dump.find(pattern)

    if index == -1:
        missing.append(name)

    while index != -1:
        retrieve_data(name, index)
        index = dump.find(pattern, index + len(pattern))

print('\nMissing')
print('\n'.join(missing))
