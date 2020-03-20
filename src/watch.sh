# Robert Durst
# 2/22/19
# Modified from the following:
# https://superuser.com/questions/181517/how-to-execute-a-command-whenever-a-file-changes
# https://stackoverflow.com/questions/407184/how-to-check-the-extension-of-a-filename-in-a-bash-script

start=$SECONDS
inotifywait -e modify -m -r . |
while read -r directory events filename; do
if [ ${filename: -5} = ".java" ]; then
   # five second delay since this for some reason fires off three times per modify and each compilation
   # can be slow
   duration=$(( SECONDS - start ))	
   if [ "$duration" -gt 5 ]; then
      start=$SECONDS
      echo "Modified: $filename, recompiling!"  
      make;
      clear;
      make run;
  fi
fi
done
