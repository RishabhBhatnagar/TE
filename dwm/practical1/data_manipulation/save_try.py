import sqlite3

DROP_outlet = '''DROP TABLE outlet'''

CREATE_outlet = '''CREATE TABLE outlet(
	oid int PRIMARY KEY,
	neighbourhood varchar(10)
	)
'''

INSERT_outlet1 = '''INSERT INTO outlet VALUES(1, 'rich') '''
SELECT_outlet_all = '''SELECT * FROM outlet'''

conn = sqlite3.connect('example.db')

c = conn.cursor()

c.execute(DROP_outlet)
c.execute(CREATE_outlet)
c.execute(INSERT_outlet1)

for inum, row in enumerate(c.execute(SELECT_outlet_all)):
	print(inum, row)




conn.commit()
conn.close()


