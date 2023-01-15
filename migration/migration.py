import os
import urllib

import py7zr
from sqlalchemy import create_engine, Table, Integer, String, Column, MetaData, ForeignKey, Time

TESTS_DIR = os.getenv("DISK_TESTS")
TESTS_ZIP = os.getenv("TESTS_ZIP")
if TESTS_DIR is None:
    TESTS_DIR = "/home/ramizouari/Academic/Excellentia/tests"
if TESTS_ZIP is None:
    TESTS_ZIP = "/home/ramizouari/Academic/Excellentia/tests.7z"


class Problems:
    def __init__(self,name:str,description:str=None,time_limit:int=None,id:int=None):
        self.id = id
        self.name = name
        self.description = description
        self.time_limit = time_limit

#with py7zr.SevenZipFile(TESTS_ZIP, mode='r') as z:
#    z.extractall(path=TESTS_DIR)

def normalize_tests(tests_dir):
    problems = []
    for dirs in os.listdir(tests_dir):
        counter = 1
        for file in os.listdir(os.path.join(tests_dir, dirs)):
            if file.endswith(".in"):
                print(file)
                os.rename(os.path.join(tests_dir, dirs, file), os.path.join(tests_dir, dirs, f"{counter:03d}.in"))
                os.rename(os.path.join(tests_dir, dirs, file.replace(".in", ".ans")), os.path.join(tests_dir, dirs, f"{counter:03d}.ans"))
                counter += 1
        problems.append(Problems(id=len(problems),name=dirs, description="None", time_limit=1))
    return problems

problems=normalize_tests(TESTS_DIR)

import pyodbc
server = 'excellentia-sql-server.database.windows.net'
database = 'excellentia'
username = 'excellentia'
password = 'gl5@12345678'
port=1433
driver= '{ODBC Driver 18 for SQL Server}'

#with pyodbc.connect('DRIVER='+driver+';SERVER=tcp:'+server+';PORT=1433;DATABASE='+database+';UID='+username+';PWD='+ password) as conn:
#    pass

from sqlalchemy.engine import URL

odbc_str = 'DRIVER='+driver+';SERVER='+server+';PORT=1433;UID='+username+';DATABASE='+ database + ';PWD='+ password
connect_str = 'mssql+pyodbc:///?odbc_connect=' + urllib.parse.quote_plus(odbc_str)

engine = create_engine(connect_str)
engine.connect()
metadata_obj = MetaData()
Problems = Table(
    "Problem",
    metadata_obj,
    Column("id", Integer, primary_key=True,autoincrement=True),
    Column("name", String(32), nullable=False,unique=True),
    Column("time_limit", Integer,nullable=False),
    Column("description", String(128), nullable=False),
)

Runs= Table(
    "Run",
    metadata_obj,
    Column("id", Integer, primary_key=True,autoincrement=True),
    Column("problem_id", Integer, ForeignKey("Problem.id"),nullable=False),
    Column("executed_at", Time, nullable=False),
    Column("time", Integer, nullable=True),
    Column("memory", Integer, nullable=True),
    Column("compiler", String(16), nullable=False),
    Column("verdict", String(16), nullable=False))

metadata_obj.drop_all(engine)
metadata_obj.create_all(engine)

with engine.begin() as connection:
    for problem in problems:
        P=problem.__dict__.copy()
        P.pop("id")
        connection.execute(Problems.insert(), P)