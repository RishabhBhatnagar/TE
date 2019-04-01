from itertools import permutations, combinations
debug_print = print
dataset = [
    [2, 5],
    [1, 2, 3, 5],
    [2, 3, 5],
    [1, 3, 4],
]
min_conf = 0.7


def algo(data, min_support=2, conf=0.7):
    debug_print("Min Count: {}\tConfidence: {}".format(min_support, conf))
    item_set = set([element for row in dataset for element in row])
    candidate_set_i, candidate_sets = 1, {}
    while 1:
        candidate_set = {}
        _permutations = map(lambda x: tuple(sorted(x)), permutations(item_set, candidate_set_i))
        for perm in _permutations:
            count = 0
            for row in data: 
                if set(perm).issubset(row): count += 1
            if count >= min_support: candidate_set[perm] = count
        debug_print("\nCandidate Set:", candidate_set_i)
        for k, v in candidate_set.items(): debug_print(k,'->', v)
        candidate_sets[candidate_set_i] = candidate_set
        candidate_set_i += 1
        if len(candidate_set.keys()) <= 2: break
    return candidate_sets


def get_sup_count(c_sets, ele):
    return c_sets[len(ele)][tuple(ele)]
def get_conf(c_sets, ele, counter_ele):
    return get_sup_count(c_sets, ele) / get_sup_count(c_sets, counter_ele) 

candidate_sets = algo(dataset)
debug_print("\nAssociation Rules:")

chosen_rules = []
for elements in candidate_sets[max(list(candidate_sets))]:
    for i in range(len(elements)-1):
        for _comb in combinations(elements, i+1):
            _comb = tuple(sorted(_comb))
            counterpart = tuple(sorted(set(elements).difference(_comb)))
            conf = get_conf(candidate_sets, sorted(_comb+counterpart), counterpart)
            # conf = min(1.0, conf)
            if conf > min_conf:
                chosen_rules.append((_comb, counterpart, conf))
            debug_print("{} -> {},  Confidence: {:.3f}".format(_comb, counterpart, conf))
    print("\nChosen Rules:")
    for rule in chosen_rules:
        debug_print("{} -> {}, Confidence: {:.3f}".format(*rule))
