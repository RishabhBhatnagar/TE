from pprint import pprint as pp


def get_random_clusters(l, n_parts):
    for i in range(n_parts):
        yield l[i::n_parts]


def get_mean(arr):
    if arr:
        return sum(arr)/len(arr)
    return 0


def is_difference_significant(l1, l2, threshold):
    diff_sum = sum(i-j for i, j in zip(l1, l2))
    return 1 if abs(diff_sum) > abs(threshold) else 0


def relocate(means, data, k):
    clusters = [[] for i in range(k)]
    for data_point in data:
        closest_idx = means.index(min(means, key=lambda x: abs(x-data_point)))
        clusters[closest_idx].append(data_point)
    return clusters


def kmeans(k, data):
    clusters = list(get_random_clusters(data, k))
    cluster_means = [get_mean(cluster) for cluster in clusters]
    prev_means = cluster_means
    epoch_number = 0
    while True:
        clusters = relocate(prev_means, data, k)
        curr_means = [get_mean(cluster) for cluster in clusters]
        print('Iteration {}'.format(epoch_number), end=' ')
        pp({tuple(cluster):mean for cluster, mean in zip(clusters, curr_means)})
        if not is_difference_significant(prev_means, curr_means, 0):
            break
        epoch_number += 1
        prev_means = curr_means
        
    


if __name__ == '__main__':
    kmeans(k=2, data=[2, 4, 10, 12, 3, 20, 30, 11, 25])
