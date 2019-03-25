#!/usr/bin/env python3.7

from flask import Flask, app, render_template, request, redirect
import sqlite3
import database_manager as db_manager

app = Flask(__name__)


# following variables are globals that either final or 
# will be filled in some methods.
DB_NAME = "Rishabh"
db = None

def get_max_score(db):
    scores = [row['score'] for row in db.get_table("score")]
    scores.append(0)        # appending 0 to tackle empty list.
    return max(scores)
    
@app.route("/login_form", methods=["GET", 'POST'])
def redirect_login():
    """User was redirected from registeration form."""
    return render_template("login.html")


@app.route("/registeration_form", methods=["GET", 'POST'])
def redirect_register():
    """User was redirected from login form."""
    return render_template("register.html")


@app.route("/login", methods=["GET"])
def login():
    """This is intentionally kept as get request to 
    allow attacker get the access to database."""

    # getting details from user form.
    uname = request.args.get("uname")
    pwd = request.args.get("pwd")

    # getting the entry of user from database.
    user = list(db.get_table_where("user"," uname='{}'".format(uname)))
    print(user)

    if not user:
        # User not found
        # redirecting to registration form.
         return "<script>\
                    alert('Sorry, you are not a registered user.');\
                    window.location.href = '/registeration_form'\
                </script>\
                 "

    # user was found...
    # checking if the password is correct:
    user = list(db.get_table_where("user"," uname='{}' and pwd='{}'".format(uname, pwd)))
    if not user:
        # correct username but wrong password:
        return """<script>  
                alert('Sorry , you are not a authenticated.');
                window.location.href = '/login_form'
                </script>"""

    # user was authenticated...
    return render_template(
            "snake_game.html", 
            username=uname, 
            time_score=list(db.get_table_where("score", "uname='{}'".format(uname))),
            max_score=get_max_score(db))


@app.route("/add_score_to_db", methods=["GET"])
def add_score():
    uname = request.args.get("uname")
    score = request.args.get("score")
    if uname and score:
        db.add_to_table("score", dict(score=score, uname=uname))
        a = list(db.get_table_where("score", "uname='{}'".format(uname)))
        return render_template(
                    "snake_game.html", 
                    username=uname, 
                    time_score=a,
                    max_score=get_max_score(db)
                )
    return "Failed to add in database."


@app.route("/register", methods=["GET"])
def register():
    """adding user to db."""
    # getting fields from html form.
    uname = request.args.get("uname")
    pwd = request.args.get("pwd")

    # check if user has already registered earlier:
    prev_user = list(db.get_table_where("user", "uname='{}'".format(uname)))
    if prev_user:
        # redirect user to login.
        return "<script>\
                    alert('You are already registered...');\
                    window.location.href = '/login_form'\
                </script>\
                "
    else:
        # created new user.
        db.execute_query("insert into user values('{}', '{}');".format(uname, pwd))
        return render_template("login.html") 

@app.route("/")
def index():
    """
    This is the first page that will be loaded. Ideally, it should be login page.
    """
    return render_template("login.html")


if __name__ == '__main__':
    db = db_manager.DataBase(DB_NAME)
    db.execute_query("create table if not exists user(uname varchar(50), pwd varchar(20));")
    db.execute_query("create table if not exists score(uname varchar(50), score integer, Timestamp datetime default current_timestamp)")
    app.run(debug=True)

