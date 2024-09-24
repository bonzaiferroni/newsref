/*
 * ATTENTION: An "eval-source-map" devtool has been used.
 * This devtool is neither made for production nor for readable output files.
 * It uses "eval()" calls to create a separate source file with attached SourceMaps in the browser devtools.
 * If you are trying to read the output file, select a different devtool (https://webpack.js.org/configuration/devtool/)
 * or disable the default devtool with "devtool: false".
 * If you are looking for production-ready output files, see mode: "production" (https://webpack.js.org/configuration/mode/).
 */
(function webpackUniversalModuleDefinition(root, factory) {
	if(typeof exports === 'object' && typeof module === 'object')
		module.exports = factory();
	else if(typeof define === 'function' && define.amd)
		define([], factory);
	else if(typeof exports === 'object')
		exports["serverjs"] = factory();
	else
		root["serverjs"] = factory();
})(this, () => {
return /******/ (() => { // webpackBootstrap
/******/ 	var __webpack_modules__ = ({

/***/ "./kotlin/streetlight-serverjs.js":
/*!****************************************!*\
  !*** ./kotlin/newsref-serverjs.js ***!
  \****************************************/
/***/ ((module) => {

eval("(function (_) {\n  'use strict';\n  //region block: pre-declaration\n  //endregion\n  function set_host(_set____db54di) {\n    host = _set____db54di;\n  }\n  function get_host() {\n    return host;\n  }\n  var host;\n  function init(h, eventId) {\n    host = h;\n    refreshEvent(eventId);\n  }\n  function refreshEvent(eventId) {\n    console.log('refreshing event');\n  }\n  //region block: init\n  host = 'http://localhost:8080';\n  //endregion\n  return _;\n}(module.exports));\n\n//# sourceURL=[module]\n//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly9zZXJ2ZXJqcy8uLi8uLi8uLi8uLi9zZXJ2ZXJqcy9zcmMvanNNYWluL2tvdGxpbi9zdHJlZXRsaWdodC9zZXJ2ZXJqcy9tYWluLmt0PzRmOGYiXSwic291cmNlc0NvbnRlbnQiOlsicGFja2FnZSBzdHJlZXRsaWdodC5zZXJ2ZXJqc1xuXG52YXIgaG9zdCA9IFwiaHR0cDovL2xvY2FsaG9zdDo4MDgwXCJcblxuZnVuIGluaXQoaDogU3RyaW5nLCBldmVudElkOkludCkge1xuICAgIGhvc3QgPSBoXG4gICAgcmVmcmVzaEV2ZW50KGV2ZW50SWQpXG59XG5cbmZ1biByZWZyZXNoRXZlbnQoZXZlbnRJZDogSW50KSB7XG4gICAgY29uc29sZS5sb2coXCJyZWZyZXNoaW5nIGV2ZW50XCIpXG59Il0sIm5hbWVzIjpbIjxzZXQtaG9zdD4iLCI8c2V0LT8+IiwiPGdldC1ob3N0PiIsImluaXQiLCJoIiwiZXZlbnRJZCIsInJlZnJlc2hFdmVudCJdLCJtYXBwaW5ncyI6Ijs7OzttQkFFQUEsQ0FBQUMsY0FBQUQsRUFBQTtBLElBQUEscUI7RUFBaUMsQzttQkFBakNFLENBQUFBLEVBQUE7QSxJQUFBLFc7RUFBaUMsQzs7ZUFFakNDLENBQVNDLEMsRUFBV0MsT0FBcEJGLEVBQWlDO0EsSUFDN0IsT0FBTyxDO0lBQ1AsYUFBYSxPQUFiLEM7RUFDSixDO3VCQUVBRyxDQUFpQkQsT0FBakJDLEVBQStCO0EsSUFDM0IsT0FBUSxLQUFJLGtCQUFKLEM7RUFDWixDOztTQVRXLHVCOzs7OyIsImZpbGUiOiIuL2tvdGxpbi9zdHJlZXRsaWdodC1zZXJ2ZXJqcy5qcyIsInNvdXJjZVJvb3QiOiIifQ==\n//# sourceURL=webpack-internal:///./kotlin/newsref-serverjs.js\n");

/***/ })

/******/ 	});
/************************************************************************/
/******/ 	// The module cache
/******/ 	var __webpack_module_cache__ = {};
/******/ 	
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/ 		// Check if module is in cache
/******/ 		var cachedModule = __webpack_module_cache__[moduleId];
/******/ 		if (cachedModule !== undefined) {
/******/ 			return cachedModule.exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = __webpack_module_cache__[moduleId] = {
/******/ 			// no module.id needed
/******/ 			// no module.loaded needed
/******/ 			exports: {}
/******/ 		};
/******/ 	
/******/ 		// Execute the module function
/******/ 		__webpack_modules__[moduleId](module, module.exports, __webpack_require__);
/******/ 	
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/ 	
/************************************************************************/
/******/ 	
/******/ 	// startup
/******/ 	// Load entry module and return exports
/******/ 	// This entry module is referenced by other modules so it can't be inlined
/******/ 	var __webpack_exports__ = __webpack_require__("./kotlin/streetlight-serverjs.js");
/******/ 	
/******/ 	return __webpack_exports__;
/******/ })()
;
});