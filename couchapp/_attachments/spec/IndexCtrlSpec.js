describe("my test suite", function() {

    beforeEach(module("filecabinet"));

    var $scope, $http, $rootScope, createController;

    beforeEach(inject(function($injector) {
        $rootScope = $injector.get("$rootScope");
        $scope = $rootScope.$new();

        var $controller = $injector.get("$controller");

        createController = function() {
            return $controller("IndexCtrl", {
                "$scope": $scope
            });
        };
    }));

    it("should affect all tags' visibility", function() {
        createController();
        $scope.tags = [
            { name: "first name", show: true},
            { name: "second name", show: false}
        ];

        $scope.showAllTags(true);
        $scope.tags.forEach(function(e){ expect(e.show).toBe(true); });

        $scope.showAllTags(false);
        $scope.tags.forEach(function(e){ expect(e.show).toBe(false); });
    });
});
