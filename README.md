# Example Chat Server

```bash
docker run --name chat_database \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=chat_database \
  -v $HOME/work/docker/mysql/chat_database:/var/lib/mysql \
  -p 3310:3306 \
  -d mysql \
  mysqld \
  --datadir=/var/lib/mysql \
  --user=mysql \
  --server-id=1 \
  --log-bin=/var/log/mysql/mysql-bin.log \
  --binlog_do_db=example \
  --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```
