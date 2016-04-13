<?php
require 'vendor/autoload.php';
use MongoDB\Client;
use MongoDB\Collection;
$dbhost = 'localhost';
	$dbname = 'Lab2';

	// Connect to test database
//	$m = new Mongo("mongodb://$dbhost");
	$client = new Client("mongodb://localhost:27017");
    $client->Lab2->Employee->insertOne(array("x"=>100000000));
//foreach ($client->Lab2->Employee->listIndexes() as $databaseInfo) {
//    var_dump($databaseInfo);
//}
//	$db = $client->selectDatabase($dbname);
//	var_dump($db);
   echo "Connection to database successfully";
   
?>