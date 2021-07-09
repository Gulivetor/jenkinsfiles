node ('windows') {
    stage ('read properties from webhook json') {
       echo 'Hello World webhook'
       echo 'git repository name is :' + repository_name
       echo 'author of commit is :' + commit_author
    }
}