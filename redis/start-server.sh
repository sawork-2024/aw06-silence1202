
for d in ./*/ ; do (cd "$d" && redis-server ./redis.conf &); done


redis-cli --cluster create 127.0.0.1:6000 127.0.0.1:6001 \
127.0.0.1:6002 127.0.0.1:6003 127.0.0.1:6004 127.0.0.1:6005 \
--cluster-replicas 1 &