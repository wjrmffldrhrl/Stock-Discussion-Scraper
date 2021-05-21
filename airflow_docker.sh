docker run -d -p 8080:8080 --name airflow -v `pwd`/dags:/usr/local/airflow/dags \
	-v `pwd`/vol:/usr/local/airflow/vol \
	puckel/docker-airflow webserver
