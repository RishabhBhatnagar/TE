from random import choice


def get_primes(max_n):
    a = [1] * max_n
    a[0] = a[1] = 0
    for (i, isprime) in enumerate(a):
        if isprime:
            yield i
            for n in range(i*i, max_n, i):
                a[n] = False


def algo(x, y, n, g):
    a = (g**x)%n
    b = (g**y)%n
    print("A: {}\tB: {}".format(a, b))
    
    k1 = (b**x)%n
    k2 = (a**y)%n
    
    try:
        assert k1 == k2
        print("Secret key value: {}".format(k1))
    except:
        print("Some implementation error...")


def main():
    primes = list(get_primes(10000))
    n, g = choice(primes), choice(primes)
    x, y = int(input("Enter value of x: ")), int(input("Enter value of y: "))
    print("n: {}\tg: {}".format(n, g))
    print("x: {}\ty: {}".format(x, y))
    algo(x, y, n, g)


if __name__ == '__main__':
    main()


# Aim: To implement diffie hellman algorithm.


'''
Output:
Enter value of x: 5
Enter value of y: 13
n: 5879	g: 463
x: 5	y: 13
A: 1659	B: 4892
Secret key value: 5068
'''
'''
Output:

'''
