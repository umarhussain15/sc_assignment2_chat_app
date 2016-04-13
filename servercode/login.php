<?php
$response=array();
$response["success"]=-1;
$response["errors"]="";
$response["results"]=array();

if (  isset($_POST["username"]) && isset($_POST["password"]) && isset($_POST["type"])){
    require_once __DIR__ . './nosql_connect.php';
    $name= $_POST["username"];
    $exp= $_POST["password"];
    $db= new NOSQLDB_CONNECT();
    $db_c= $db->connect();
    
    // Continue Work

    $count= $db_c->users->count(array("username"=>$name));
    // var_dump($qry);
    // echo "hello count is ".$count;
    if ($count==0 && $_POST["type"]=="signup"){
            $qry= $db_c->users->insertOne(array("username"=>$name,"password"=>$exp,"gender"=>$_POST["gender"]));
            $response["success"]=1;
    }
    else if ($count==0 && $_POST["type"]=="login"){
        $response["errors"]="no user exists";
    }
    else if (1== $db_c->users->count(array("username"=>$name,"password"=>$exp)) && $_POST["type"]=="login"){
        $db_c->users->findOneAndUpdate(array("username" => $name),
                             array('$set' => array("lat" => $_POST["lat"], "lng" => $_POST["lng"])));
        $response["success"]=1;
    }
        
//    var_dump($_POST);
    echo json_encode($response);
}
else{
    $response["errors"]="error";
    $response["success"]=-2;
    echo json_encode($response);
}
?>