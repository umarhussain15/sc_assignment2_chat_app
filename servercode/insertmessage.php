<?php
$response=array();
$response["success"]=-1;
$response["errors"]="";
$response["results"]=array();

if (isset($_POST["username"])  && isset($_POST["receiver"]) && isset($_POST["type"])){
    require_once __DIR__ . './nosql_connect.php';
    $name= $_POST["username"];
    
    $receiver= $_POST["receiver"];
    $db= new NOSQLDB_CONNECT();
    $db_c= $db->connect();
    $str=$name.":".$receiver;
    // Continue Work
        if ($_POST["type"]=="msg" && isset($_POST["message"])){
            $content=array("username"=>$name,"message"=>$_POST["message"],"receiver"=>$receiver,"type"=>$_POST["type"],"str"=>$str);
        $qry= $db_c->user_messages->insertOne($content,array('fsync' => 1));
            $response["insertid"]=$qry->getInsertedId()."";
    }
    else if ($_POST["type"]=="loc"){
        
        $content=array("username"=>$name,"receiver"=>$receiver,"str"=>$str,"type"=>$_POST["type"],
                       "lat"=>$_POST["loc"],"lng"=>$_POST["lng"]);
         $qry= $db_c->user_messages->insertOne($content,array('fsync' => 1));
//        var_dump($qry->getInsertedId());
// echo $qry->getInsertedId();
        $response["insertid"]=$qry->getInsertedId()."";
    }else{
        
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