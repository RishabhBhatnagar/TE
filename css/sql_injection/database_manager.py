import sqlite3
import json
import os
import types

# defining  all the constants.
if True:
    # defining all the constants which wil be used.
    debug_print = print


class DataBase:
    """
    Description:
        This class handles the all the db operations without
        intervening/mixing with bc's implementation.
    Implementation Detail:
        Uses sqlite3 database for storing.
        The database is stored in the current directory locally.
    """

    def __init__(self, db_name):
        """
        This function creates a new database if db of given name is not existing in the current directory.
        :param db_name: name of the database.
        """
        # uses only db_name to make a db
        self.db_name = db_name

    def get_db_connection(self):
        overwrite_database = None
        # checking if there is an existing db with given name.
        if os.path.exists(self.db_name + ".db"):
            # found an existing instance of the passed db name.
            print("Found an existing database named {}".format(self.db_name))

            # asking user if database can be overwritten.
            ip = input("Do you want to overwrite current database (y/n)??").lower()

            # Following line required when user didn't enter anything(defaults to 'y').
            overwrite_database = 'y' if not ip else ip[0]
        else:
            # permitting control to create a new db when there is no existing db.
            overwrite_database = 'y'

        if overwrite_database == 'y':
            connection = sqlite3.connect(self.db_name)
            return connection
        else:
            return sqlite3.connect(self.db_name)

    def execute_query(self, query):
        connection = self.get_db_connection()
        result = None
        try:
            debug_print("###", query)
            result = connection.executescript(query)
            connection.commit()
        except sqlite3.OperationalError as e:
            raise Exception("Couldn't execute query. Your query is : \n{}\nError Occured: {}".format(query, e))
        connection.close()
        return result

    def add_to_table(self, table_name, values):
        """
        This will add the passed values to the existing table with name as table_name.
        :param table_name: name of the db table in which you want to enter new row.
        :param values: The values which are to be entered in the database.
        :return: None
        """

        # There are two versions of this function based on the data-type of values.
        #     1. When values is of type iterable:
        #          This is used when user knows the order in which data is stored in cols in the database.
        #          This is rarely used in production systems.
        #     2. When values is a dict:
        #          User is unsure of the order of cols in the db.
        #          User passes a dict of {col_name:val for col_name in db.cols}
        #          This will be majorly used.

        query = None
        if type(values) in [type([]), type(()), types.GeneratorType]:
            # adding only tuple, list and generator in options of iterables.

            # creating a base query with only one value.
            query = 'INSERT INTO {} VALUES ({}'.format(table_name, values.pop(0))
            for val in values:
                # adding all the other values in the query.
                query += ", " + val
            # terminating  the query after appending all the vals in the values with a semicolon.
            query += ');'

        elif type({}) == type(values):
            # user is not sure about the order of the cols in the db.
            if len(values) == 1:
                # if only one element is present in the table, order doesn't matter due to reflexive property.
                return self.add_to_table(table_name, [list(values.values())[0]])

            query = "INSERT INTO {}{} VALUES {}".format(
                table_name,
                tuple(values.keys()),
                tuple(values.values())
            )
            debug_print("@@@", query)
        else:
            raise NotImplementedError("Couldn't create row.")

        # we got a query to execute.
        connection = self.get_db_connection()
        connection.executescript(query)
        connection.commit()
        connection.close()

    def del_database(self):
        # deleting the db when db object is deleted.
        debug_print("%%$ deleting database instance\ n")
        if os.path.exists(self.db_name):
            os.remove(self.db_name)


    def get_table(self, table_name):
        # This will return all the contents of the  table from the current db object instance
        connection = self.get_db_connection()
        connection.row_factory = sqlite3.Row
        cursor = connection.cursor()
        try:
            cursor.executescript('select * from {} where 1==1'.format(table_name))
        except sqlite3.OperationalError as e:
            raise sqlite3.OperationalError(e)
        for row in cursor.fetchall():
            yield row

        connection.close()
    
    def get_table_where(self, table_name, where_clause):
        # This will return all the contents of the  table from the current db object instance
        connection = self.get_db_connection()
        connection.row_factory = sqlite3.Row
        cursor = connection.cursor()
        try:
            query = 'select * from {} where {}'.format(table_name, where_clause)
            print(query)
            cursor.executescript(query)
        except sqlite3.OperationalError as e:
            raise sqlite3.OperationalError(e)
        for row in cursor.fetchall():
            yield dict(row)

        connection.close()
