 var app = angular.module("mainApp", []);
    app.controller("mainCtrl", function($scope, $http) {
    	// values bound to view
    	$scope.editMode = false;
		$scope.editTaskId = null; 
    	$scope.newUserFirst = null;
    	$scope.newUserLast = null;
    	$scope.selectedUser = null;
    	$scope.updateTaskTitle = null;
    	$scope.updateTaskDescription = null;
    	$scope.updateTaskDueDate = null;
    	$scope.updateTaskState = null;
    	$scope.updateTaskCategory = null;
    	$scope.updateTaskPriority = null;
    	
    	// constant values
    	$scope.states = ["To Do", "Doing", "Done"];
    	$scope.priorities = ["1","2","3","4","5"];
    	
    	updateUserDropDown();
    	
    	$scope.getTasks = function(){
    		if($scope.selectedUser != null){
    			$http.get("\GetTasks?userId=" + $scope.selectedUser.id).then(function(response){
    				var tasks = response.data.split("/split\n");
    				tasks.pop();
    				var taskData = [];
    				angular.forEach(tasks, function(task){
    					var taskInfo = task.split("/split,");
    					var taskObj = Object.create({});
    					taskObj.id = taskInfo[0];
    					taskObj.title = taskInfo[1];
    					taskObj.description = taskInfo[2];
    					taskObj.duedate = taskInfo[3];
    					taskObj.state = getStateById(taskInfo[4]);
    					taskObj.category = taskInfo[5];
    					taskObj.priority = taskInfo[6];
    					taskData.push(taskObj);
    				});
    				
    				$scope.tasks = taskData;
    			});
    		}
    	}
    	
    	$scope.addTask = function(){
    		$scope.editMode = true; 
    	}
    	
    	$scope.editTask = function(task){
    		$scope.editMode = true;
    		$scope.editTaskId = task.id; 
    		$scope.updateTaskTitle = task.title;
        	$scope.updateTaskDescription = task.description;
        	$scope.updateTaskDueDate = new Date(task.duedate);
        	$scope.updateTaskState = task.state;
        	$scope.updateTaskCategory = task.category;
        	$scope.updateTaskPriority = task.priority;
    	}
    	
    	$scope.cancelEdit = function(){
    		$scope.editMode = false;
    		$scope.editTaskId = null;
    	}
    	
    	$scope.saveTaskUpdates = function(){
    		if($scope.editTaskId === null){
    			$scope.editTaskId = "0";
    		}
    		if($scope.updateTaskDueDate != null && $scope.updateTaskDueDate != ""){
    			$scope.updateTaskDueDate = ($scope.updateTaskDueDate.getMonth() + 1) + "/" + $scope.updateTaskDueDate.getDate() + "/" +  $scope.updateTaskDueDate.getFullYear();
    		}
    		if($scope.updateTaskState!=null && $scope.updateTaskState!="" ){
    			$scope.updateTaskState = getIdByState($scope.updateTaskState)
    		}
    		var data = [$scope.editTaskId,
    		$scope.selectedUser.id,
    		$scope.updateTaskTitle,
        	$scope.updateTaskDescription,
        	$scope.updateTaskDueDate,
        	$scope.updateTaskState,
        	$scope.updateTaskCategory,
        	$scope.updateTaskPriority].join("/split,");
    		$http.post("\GetTasks", data).then(function(response){
    			$scope.getTasks();
    			$scope.cancelEdit();
    		});
    	}
    	
    	$scope.addUser = function(){
    		var data = $scope.newUserLast + "/split," + $scope.newUserFirst;
    		$http.post("\GetUsers", data).then(function(response){
    			updateUserDropDown();
    		});
    	}
    	
    	function updateUserDropDown(){
    		$http.get("\GetUsers").then(function(response){
    			var users = response.data.split("/split\n");
    			users.pop();
    			var userData = [];
    			angular.forEach(users, function(user){
    				var userInfo = user.split("/split,");
    				var userObj = Object.create({});
    				userObj.last = userInfo[1];
		            userObj.first = userInfo[2];
    				userObj.id = userInfo[0];
    				userData.push(userObj);
    			});
    			
    			$scope.users = userData;
    			$scope.selectedUser = userData[0];
    			$scope.tasks = [];
    		});
    	}
    	
    	function getStateById(id){
    		var trimmedId = id.trim();
    		if(trimmedId === "1"){
    			return "To Do";
    		}
    		else if(trimmedId === "2"){
    			return "Doing";
    		}
    		else{
    			return "Done";
    		}
    	}
    	
    	function getIdByState(state){
    		if(state === "To Do"){
    			return "1";
    		}
    		else if(state === "Doing"){
    			return "2";
    		}
    		else{
    			return "3";
    		}
    	}
    });