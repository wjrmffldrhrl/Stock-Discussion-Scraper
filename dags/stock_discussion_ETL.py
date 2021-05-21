from airflow import DAG
from airflow.operators import PythonOperator
from datetime import datetime
import psycopg2
import requests
import os


dag = DAG(
    dag_id = 'stock_discusson_ETL',
    start_date = datetime(2021,4,27),
    schedule_interval = '0 1 * * *'
)

def read_info():
    info = {}
    info_file = open('./redshift.info', 'r')
    info['host'] = info_file.readline().split('host : ')[1].strip()
    info['user'] = info_file.readline().split('user : ')[1].strip()
    info['password'] = info_file.readline().split('password : ')[1].strip()
    info['port'] = info_file.readline().split('port : ')[1].strip()
    info['dbname'] = info_file.readline().split('dbname: ')[1].strip()
    info_file.close()
    return info

# Redshift connection 함수
def get_Redshift_connection():
    info = read_info()
    host = info['host']
    redshift_user = info['user']
    redshift_pass = info['password']
    port = info['port']
    dbname = info['dbname']
    conn = psycopg2.connect("dbname={dbname} user={user} host={host} password={password} port={port}".format(
        dbname=dbname,
        user=redshift_user,
        password=redshift_pass,
        host=host,
        port=port
    ))
    conn.set_session(autocommit=True)
    return conn.cursor()

def find_stock_dirs(path):
    stock_dirs = []


    try:
        filenames = os.listdir(path)
        for filename in filenames:
            full_filename = os.path.join(path, filename)
            if os.path.isdir(full_filename):
                stock_dirs.append(full_filename)

    except PermissionError:
        pass

    return stock_dirs

def find_csv(path):
    csv_list = []

    try:
        filenames = os.listdir(path)
        for filename in filenames:
            full_filename = os.path.join(path, filename)
            if os.path.isdir(full_filename):
                pass
            else:
                ext = os.path.splitext(full_filename)[-1]
                if ext == '.csv': 
                    csv_list.append(full_filename)

    except PermissionError:
        pass

    return csv_list


# excution_date 가 '2021.04.17'일때
# context['prev_ds'] =  '2021-04-16'
def extract(**context):

    target_date = context['prev_ds'].replace('-','_')
    stock_dirs = find_stock_dirs(context['params']['path'])

    for dir in stock_dirs:
        print(dir)
        








def transform(**context):
    pass

def load(**context):
    pass




extract = PythonOperator(
    task_id = 'extract',
    python_callable = extract,
    params={'path': ""},
    provide_context=True,
    dag = dag
)

transform = PythonOperator(
    task_id = 'transform',
    python_callable = transform,
    provide_context=True,
    dag = dag
)


load = PythonOperator(
    task_id = 'load',
    python_callable = load,
    provide_context=True,
    dag = dag
)


extract >> transform >> load