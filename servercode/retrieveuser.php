<?php
$response=array();
$response["success"]=-1;
$response["errors"]="";
$response["results"]=array();
$response["count"]=0;
if (isset($_POST["username"])){
    require_once __DIR__ . './nosql_connect.php';
   
    $db= new NOSQLDB_CONNECT();
    $db_c= $db->connect();
    
    // Continue Work
    $cursor= $db_c->users->find(array('username'=>array('$nin'=>array($_POST["username"]))));
//    $cursor= $db_c->messages->find(array('username'=>array('$nin'=>$_POST["username"])));
    $it= iterator_to_array($cursor);
//    var_dump($it);
    foreach ($it as $doc) {
        $product=array();
        $product["username"]=$doc["username"];
        
        $product["gender"]=$doc["gender"];
    
        array_push($response["results"],$product);
        $response["count"]++;
   //var_dump($doc);
    }

    $response["success"]=1;
    echo json_encode($response);
}
else{
    $response["errors"]="error";
    $response["success"]=-2;
    echo json_encode($response);
}
?>