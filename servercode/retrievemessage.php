<?php
$response=array();
$response["success"]=-1;
$response["errors"]="";
$response["results"]=array();
$response["lastmessage"]="-1";
if (isset($_POST["name"]) && isset($_POST["lastmessage"]) && isset($_POST["receiver"])){
    require_once __DIR__ . './nosql_connect.php';
   
    $db= new NOSQLDB_CONNECT();
    $db_c= $db->connect();
    $incheck=array($_POST["name"].":".$_POST["receiver"],$_POST["receiver"].":".$_POST["name"]);
//    var_dump($incheck);
    // Continue Work
    $response["lmval"]=$_POST["name"].":".$_POST["receiver"];
//    if($_POST["lastmessage"]!="-1"){
//    $cursor= $db_c->user_messages->find(array('str'=>array('$in'=>$incheck),"_id"=>array('$gte'=>$_POST["lastmessage"])));
//    }
//    else{
       $cursor= $db_c->user_messages->find(array('str'=>array('$in'=>$incheck))); 
//    }
//    var_dump($cursor);
    $it= iterator_to_array($cursor);
//    var_dump($it);
    $obj;
    foreach ($it as $doc) {
        $product=array();
        if ($doc["username"]==$_POST["name"])
                $product["name"]=$doc["username"]." (me)";
//                $product["name"]="me";
        else
        $product["name"]=$doc["username"];
        
        if($doc["type"]=="msg")
        $product["message"]=$doc["message"];
        else if ($doc["type"]=="loc"){
            $product["message"]='Location Shared Click to See';
            $product["lat"]=$doc["lat"];
            $product["lng"]=$doc["lng"];
        }
        else if ($doc["type"]=="image"){
            $product["message"]='Image Shared Click to See';
            
        }
        $product["type"]=$doc["type"];
        $product["messageid"]=$doc["_id"]."";
        
       //$db_c->messages->update(array("_id"=>$doc["_id"]),array('$set'=>array("read"=>1)));
        array_push($response["results"],$product);
        $response["lastmessage"]=$doc["_id"]."";
    
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