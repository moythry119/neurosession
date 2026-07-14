angular.module('neuroSession', ['ngRoute'])

    .config(function($routeProvider, $httpProvider) {

        $httpProvider.interceptors.push('authInterceptor');

        $routeProvider
            .when('/login', {
                templateUrl: 'views/login.html',
                controller: 'LoginController'
            })
            .when('/dashboard', {
                templateUrl: 'views/dashboard.html',
                controller: 'DashboardController'
            })
            .when('/participants', {
                templateUrl: 'views/participants.html',
                controller: 'ParticipantController'
            })
            .when('/monitoring', {
                templateUrl: 'views/monitoring.html',
                controller: 'MonitoringController'
            })
            .when('/participants/:id', {
                templateUrl: 'views/participant-detail.html',
                controller: 'ParticipantDetailController'
            })
            .otherwise({ redirectTo: '/login' });
    })

    .factory('authInterceptor', function($window) {
        return {
            request: function(config) {
                var token = $window.localStorage.getItem('token');
                if (token) {
                    config.headers['Authorization'] = 'Bearer ' + token;
                }
                return config;
            }
        };
    })

    .controller('LoginController', function($scope, $http, $window, $location) {
        $scope.credentials = {};
        $scope.error = '';

        $scope.login = function() {
            $http.post('/api/auth/login', $scope.credentials)
                .then(function(response) {
                    $window.localStorage.setItem('token', response.data.token);
                    $window.localStorage.setItem('email', response.data.email);
                    $window.localStorage.setItem('role', response.data.role);
                    $location.path('/dashboard');
                })
                .catch(function() {
                    $scope.error = 'Invalid email or password.';
                });
        };
    })

    .controller('DashboardController', function($scope, $window, $location) {
        $scope.email = $window.localStorage.getItem('email');
        $scope.role = $window.localStorage.getItem('role');

        $scope.logout = function() {
            $window.localStorage.clear();
            $location.path('/login');
        };
    })

    .controller('ParticipantController', function($scope, $http) {
        $scope.participants = [];
        $scope.newParticipant = {};
        $scope.error = '';
        $scope.success = '';

        $http.get('/api/participants')
            .then(function(response) {
                $scope.participants = response.data;
            })
            .catch(function(err) {
                $scope.error = 'Error ' + err.status;
            });

        $scope.create = function() {
            $scope.error = '';
            $scope.success = '';
            $http.post('/api/participants', $scope.newParticipant)
                .then(function(response) {
                    $scope.participants.push(response.data);
                    $scope.newParticipant = {};
                    $scope.success = 'Participant created successfully.';
                })
                .catch(function(err) {
                    $scope.error = 'Error: ' + (err.data.message || 'Could not create participant.');
                });
        };
    })

    .controller('MonitoringController', function($scope, $http, $window, $location) {
        $scope.email = $window.localStorage.getItem('email');
        $scope.role = $window.localStorage.getItem('role');
        $scope.progress = [];
        $scope.error = '';

        $scope.logout = function() {
            $window.localStorage.clear();
            $location.path('/login');
        };

        $http.get('/api/participants/progress')
            .then(function(response) {
                $scope.progress = response.data;
                $scope.summary = {
                    total: $scope.progress.length,
                    complete: $scope.progress.filter(function(p) { return p.status === 'Complete'; }).length,
                    missingVrh: $scope.progress.filter(function(p) { return p.status === 'Missing VRH'; }).length,
                    missingHyp: $scope.progress.filter(function(p) { return p.status === 'Missing HYP'; }).length,
                    missingBoth: $scope.progress.filter(function(p) { return p.status === 'Missing both'; }).length
                };
            })
            .catch(function(err) {
                $scope.error = 'Error ' + err.status;
            });
    })

    .controller('ParticipantDetailController', function($scope, $http, $routeParams, $location) {
        var id = $routeParams.id;
        $scope.editing = false;
        $scope.showSessionForm = false;
        $scope.sessionData = {};
        $scope.vrhSession = null;
        $scope.hypSession = null;
        $scope.success = '';
        $scope.error = '';

        function loadParticipant() {
            $http.get('/api/participants/' + id)
                .then(function(res) { $scope.participant = res.data; });
        }

        function loadSessions() {
            $http.get('/api/participants/' + id + '/sessions')
                .then(function(res) {
                    $scope.vrhSession = null;
                    $scope.hypSession = null;
                    res.data.forEach(function(s) {
                        if (s.sessionType === 'VRH') $scope.vrhSession = s;
                        if (s.sessionType === 'HYP') $scope.hypSession = s;
                    });
                });
        }

        loadParticipant();
        loadSessions();

        $scope.startEdit = function() {
            $scope.editData = angular.copy($scope.participant);
            $scope.editing = true;
        };

        $scope.cancelEdit = function() { $scope.editing = false; };

        $scope.saveEdit = function() {
            $http.put('/api/participants/' + id, $scope.editData)
                .then(function(res) {
                    $scope.participant = res.data;
                    $scope.editing = false;
                    $scope.success = 'Participant updated.';
                })
                .catch(function(err) {
                    $scope.error = 'Error saving: ' + err.status;
                });
        };

        $scope.deleteParticipant = function() {
            if (!confirm('Delete participant ' + $scope.participant.participantCode + '?')) return;
            $http.delete('/api/participants/' + id)
                .then(function() { $location.path('/participants'); });
        };

        $scope.addSession = function(type) {
            $scope.sessionData = { sessionType: type };
            $scope.sessionFormType = type;
            $scope.editingSessionId = null;
            $scope.showSessionForm = true;
        };

        $scope.cancelSession = function() {
            $scope.showSessionForm = false;
            $scope.sessionData = {};
        };

        $scope.editSession = function(type) {
            var s = type === 'VRH' ? $scope.vrhSession : $scope.hypSession;
            $scope.sessionData = angular.copy(s);
            $scope.sessionFormType = type;
            $scope.editingSessionId = s.id;
            $scope.showSessionForm = true;
        };

        $scope.saveSession = function() {
            $scope.sessionData.sessionType = $scope.sessionFormType;
            var promise;
            if ($scope.editingSessionId) {
                promise = $http.put('/api/sessions/' + $scope.editingSessionId, $scope.sessionData);
            } else {
                promise = $http.post('/api/participants/' + id + '/sessions', $scope.sessionData);
            }
            promise.then(function() {
                $scope.showSessionForm = false;
                $scope.success = 'Session saved.';
                loadSessions();
            }).catch(function(err) {
                $scope.error = 'Error saving session: ' + err.status;
            });
        };

        $scope.deleteSession = function(sessionId) {
            if (!confirm('Delete this session?')) return;
            $http.delete('/api/sessions/' + sessionId)
                .then(function() {
                    $scope.success = 'Session deleted.';
                    loadSessions();
                });
        };
    });