from random import randint
from pandas import read_csv
from pandas import DataFrame
from pandas import factorize as categorize
from string import ascii_uppercase
import pickle
csv_file_name = 'BlackFriday.csv'


def _generate_gstn():
    # General format of gstn is:
    """AACFV 9673 L 1 ZD
        AACCN 3320 K 1 ZL"""

    rand_string = "".join(ascii_uppercase[randint(0, 25)] for _ in range(8))
    rand_numbers = "".join([str(randint(0, 9)) for _ in range(5)])
    return rand_string[:5] \
           + rand_numbers[:-1] \
           + rand_string[5] \
           + rand_numbers[-1] \
           + rand_string[6:]


def generate_companies(n_companies=5):
    company = []
    for i in range(n_companies):
        company.append(
            dict(
                year_establishment=2019 - randint(3, 100),
                cid=i,
                public_sentiment=randint(1, 9),
                gstn=_generate_gstn()
            )
        )
    return DataFrame(company)


def only_numbers(string):
    """
    This will extract all the digits from the given string.
    :param string: the string which has digits.
    :return: string having all the digits of the given string.
    """
    return ''.join([e for e in string if e.isdigit()])


def choose_age(interval: str, age_limit: int = 5) -> int:
    """
    This returns a random number between the given interval
    which has to be in the given format to be parsed correctly.
    a-b / a+ / NaN,         a, b are integers.

    :param interval: the range of the age.
    :param age_limit: the function should
                      not return value below the given age limit.
    :return: randomly generated number for age in given interval.
    """

    # using simple membership operator instead of regex match.
    if '-' in interval:
        # The given range is of type a-b
        rand_age = randint(*map(int, interval.split('-')))
    elif '+' in interval:
        # given interval is of type a+
        rand_age = int(interval.split('+')[0])
    else:
        # Either the param is missing or of other format.
        # choosing random number between age_limit and 99
        rand_age = randint(age_limit + 1, 99)

    rand_age = rand_age if rand_age > age_limit else rand_age + age_limit
    return rand_age


def save_objs(pkl_file_name:str, objects:dict):
    if '.pkl' not in pkl_file_name:
        raise TypeError("A pickle file is required to store variables.")
    with open(pkl_file_name, 'wb') as op_file:
        pickle.dump(objects, op_file, pickle.HIGHEST_PROTOCOL)


def load_dataframes(pkl_file_name:str):
    # pkl file has a tuple of tuples. 
    # Each tuple having name and dataframe.
    vars_dict: (str, DataFrame)
    vars_dict = pickle.load(open('objects.pkl', 'rb'))
    globals().update(vars_dict)
    return vars_dict


def main():
    # defining the static strings that will be used further in the program.
    age = 'Age'
    product_category = "Product_Category_1"
    stay_in_current_city_years = 'stay_in_current_city_years'.title()
    temp_city_years = 'temp stay years'.title()
    gender = 'Gender'
    pid = "Product_ID"
    discount = 'Discount'
    price = "Purchase"
    max_rack_size = 5
    max_living_year = 10
    marital_status = 'Marital_status'.title()
    occupation = 'occupation'.title()
    rack_location = 'rack location'.title()
    # static strings end.
    
    # These variables will be loaded when below code is executed.
    gender_mapping = None
    data = None
    # variable definition end.
    
    data = read_csv(csv_file_name)  # getting data from csv.
    header = list(data)
    print(header)
    # Removing all the product categories from the header.
    header = [col for col in header if "product_category" not in col.lower()]

    # Keeping only one product category in the header.
    header.append(product_category)
    
    # overwriting data with subset of the orig dataset.
    data = data[header]

    # choosing random age between given range
    data[age] = data[age].apply(lambda x: choose_age(x))

    # Removing all weed characters from stay years.
    data[stay_in_current_city_years] = data[stay_in_current_city_years].apply(lambda x: only_numbers(x))

    # convert categorical data in numbers.
    data[gender], unique_genders = categorize(data[gender])
    gender_mapping = {unique_gender: i for unique_gender, i in zip(unique_genders, range(len(unique_genders)))}
    
    # adding discounts for all the products:
    pid_discount = {_pid:randint(0, 51)/100 for _pid in set(data[pid])}
    data[discount] = [pid_discount[_pid] for _pid in data[pid]]
    
    # generating location on rack (int between [0-5] (default))
    location_on_rack = {_pid:randint(0, max_rack_size)/100 for _pid in set(data[pid])}
    data[rack_location] = [location_on_rack[_pid] for _pid in data[pid]]
    
    # Temporary stay years is defined.
    data[temp_city_years] = [randint(0, max_living_year) for _ in data[pid]]
            
    # Dropping all the rows which have atleast one col as NaN.
    data = data.dropna()
    
    # seggregating the data from the DataFrame into various categories.
    company = generate_companies()
    product = data[[product_category, discount, price, rack_location]]
    user = data[[stay_in_current_city_years, temp_city_years, gender, age, occupation]]
    
    # To-do: Generate data for outlet.
    # This was not done because almost all the fields was not there in the dataset.
    # Also, for olap queries, the cube has only 3 dimensions which we already got.
    # We were told that hypercube won't be necessary.
    outlet = data[[]]
    
    save_objs('objects.pkl', dict(company=company, product=product, user=user, outlet=outlet))


if __name__ == '__main__':
    main()

