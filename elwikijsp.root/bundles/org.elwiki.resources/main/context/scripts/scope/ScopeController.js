function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
function _slicedToArray(arr, i) { return _arrayWithHoles(arr) || _iterableToArrayLimit(arr, i) || _nonIterableRest(); }
function _arrayWithHoles(arr) { if (Array.isArray(arr)) return arr; }
function _iterableToArrayLimit(arr, i) { var _arr = []; var _n = true; var _d = false; var _e = undefined; try { for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) { _arr.push(_s.value); if (i && _arr.length === i) break; } } catch (err) { _d = true; _e = err; } finally { try { if (!_n && _i["return"] != null) _i["return"](); } finally { if (_d) throw _e; } } return _arr; }
function _nonIterableRest() { throw new TypeError("Invalid attempt to destructure non-iterable instance"); }

function _animation(duration, callback) {
	requestAnimationFrame(function() {
		callback.enter();
		requestAnimationFrame(function() {
			callback.active();
			setTimeout(function() {
				callback.leave();
			}, duration);
		});
	});
}

/*
Source data:
	node {
		"id": "0",
		"text": "node-0",
		"children": []
	}
*/
STreeViewer = new Class({
	Implements: [Options],

	treeNodes: [],
	nodesById: {},
	leafNodesById: {},

	liElementsById: {},
	willUpdateNodesById: {},
	container: null,

	options: {
		selectMode: 'checkbox',
		values: [],
		disables: [],
		beforeLoad: null,
		loaded: null,
		url: null,
		method: 'GET',
		closeDepth: null
	},

	initialize: function(container, options) {
		this.setOptions(options);
		this.container = container;

		if (options.url) {
			Wiki.jsonrpc(this.options.url, ["foo_STreeViewer", 23], function(data) {
				this.initTree(data);
			}.bind(this));
		} else {
			this.initTree(options.data);
		}
	},

	initTree: function(data) {
		let _Tree$parseTreeData = this.parseTreeData(data);
		let defaultValues = _Tree$parseTreeData.defaultValues;
		let defaultDisables = _Tree$parseTreeData.defaultDisables;

		this.render(this.treeNodes);

		let values = this.options.values;
		let disables = this.options.disables;
		let loaded = this.options.loaded;

		if (values && values.length)
			defaultValues = values;
		defaultValues.length && this.setValues(defaultValues);

		if (disables && disables.length)
			defaultDisables = disables;
		defaultDisables.length && this.setDisables(defaultDisables);

		//:FVK: loaded && loaded.call(this);
	},

	parseTreeData: function(data) {
		let nodesById = {};
		let leafNodesById = {};
		let values = [];
		let disables = [];

		var walkTree = function walkTree(nodes, parent) {
			nodes.forEach(function(node) {
				nodesById[node.id] = node;
				if (node.checked) values.push(node.id);
				if (node.disabled) disables.push(node.id);
				if (parent) node.parent = parent;

				if (node.children && node.children.length) {
					walkTree(node.children, node);
				} else {
					leafNodesById[node.id] = node;
				}
			});
		};

		this.treeNodes = JSON.parse(JSON.stringify(data));
		walkTree(this.treeNodes);

		this.nodesById = nodesById;
		this.leafNodesById = leafNodesById;

		return {
			defaultValues: values,
			defaultDisables: disables
		};
	},

	render: function(treeNodes) {
		let self = this;
		let buildTree = function(nodes, depth) {
			let ulContainer = new Element('ul', { class: 'treejs-nodes' });
			if (nodes && nodes.length) {
				nodes.forEach(function(node) {
					/* Create Li element, with its features. */
					let liEle = new Element('li', { class: 'treejs-node' });
					let isClosed = depth === self.options.closeDepth - 1;
					if (isClosed) liEle.addClass('treejs-node__close');
					if (node.children && node.children.length) { // add switcher, if needed.
						liEle.appendChild(new Element('span', { class: 'treejs-switcher' }));
					} else {
						liEle.addClass('treejs-placeholder');
					}
					liEle.appendChild(new Element('span', { class: 'treejs-checkbox' })); // add checkbox.
					liEle.appendChild(new Element('span', { class: 'treejs-label', text: node.text })); // add label.
					liEle.nodeId = node.id;
					// Add descendants.
					self.liElementsById[node.id] = liEle;
					let ulEle = null;
					if (node.children && node.children.length) {
						ulEle = buildTree(node.children, depth + 1);
					}
					ulEle && liEle.appendChild(ulEle);
					ulContainer.appendChild(liEle);
				});
			}
			return ulContainer;
		};

		let treeEle = new Element('div', { class: 'treejs' }); // create root element.
		treeEle.appendChild(buildTree(treeNodes, 0)); // build tree of html elements.

		treeEle.addEvent('click', function(e) {
			let target = e.target;
			if (target.nodeName === 'SPAN' && (target.classList.contains('treejs-checkbox') || target.classList.contains('treejs-label'))) {
				this.onItemClick(target.parentNode.nodeId);
			} else if (target.nodeName === 'LI' && target.classList.contains('treejs-node')) {
				this.onItemClick(target.nodeId);
			} else if (target.nodeName === 'SPAN' && target.classList.contains('treejs-switcher')) {
				this.onSwitcherClick(target);
			}
		}.bind(self));

		// Add into container element.
		let ele = document.querySelector(self.container);
		ele.empty();
		ele.appendChild(treeEle);
	},

	/* --- --- --- --- --- Data Manipulation --- --- --- --- --- --- --- --- */

	getValues: function() {
		let values = [];

		for (let id in this.leafNodesById) {
			if (this.leafNodesById.hasOwnProperty(id)) {
				if (this.leafNodesById[id].status === 1 || this.leafNodesById[id].status === 2) {
					values.push(id);
				}
			}
		}

		return values;
	},

	setValues: function(values) {
		let self = this;

		this.emptyNodesCheckStatus();
		values.forEach(function(value) {
			self.setValue(value);
		});
		this.updateLiElements();
		var onChange = this.options.onChange;
		onChange && onChange.call(this);
	},

	setValue: function(value) {
		let node = this.nodesById[value];
		if (!node) return;
		let prevStatus = node.status;
		let status = prevStatus === 1 || prevStatus === 2 ? 0 : 2;
		node.status = status;
		this.markWillUpdateNode(node);
		this.walkUp(node, 'status');
		this.walkDown(node, 'status');
	},

	getDisables: function() {
		var values = [];

		for (var id in this.leafNodesById) {
			if (this.leafNodesById.hasOwnProperty(id)) {
				if (this.leafNodesById[id].disabled) {
					values.push(id);
				}
			}
		}

		return values;
	},

	setDisable: function(value) {
		let node = this.nodesById[value];
		if (!node) return;
		let prevDisabled = node.disabled;

		if (!prevDisabled) {
			node.disabled = true;
			this.markWillUpdateNode(node);
			this.walkUp(node, 'disabled');
			this.walkDown(node, 'disabled');
		}
	},

	setDisables: function(values) {
		let self = this;

		this.emptyNodesDisable();
		values.forEach(function(value) {
			self.setDisable(value);
		});
		this.updateLiElements();
	},

	emptyNodesCheckStatus: function() {
		this.willUpdateNodesById = this.getSelectedNodesById();
		Object.values(this.willUpdateNodesById).forEach(function(node) {
			if (!node.disabled) node.status = 0;
		});
	},

	emptyNodesDisable: function() {
		this.willUpdateNodesById = this.getDisabledNodesById();
		Object.values(this.willUpdateNodesById).forEach(function(node) {
			node.disabled = false;
		});
	},

	getSelectedNodesById: function() {
		return Object.entries(this.nodesById).reduce(function(acc, _ref) {
			var _ref2 = _slicedToArray(_ref, 2),
				id = _ref2[0],
				node = _ref2[1];

			if (node.status === 1 || node.status === 2) {
				acc[id] = node;
			}

			return acc;
		}, {});
	},

	getDisabledNodesById: function() {
		return Object.entries(this.nodesById).reduce(function(acc, _ref3) {
			var _ref4 = _slicedToArray(_ref3, 2),
				id = _ref4[0],
				node = _ref4[1];

			if (node.disabled) {
				acc[id] = node;
			}

			return acc;
		}, {});
	},

	updateLiElement: function(node) {
		let classList = this.liElementsById[node.id].classList;

		switch (node.status) {
			case 0:
				classList.remove('treejs-node__halfchecked', 'treejs-node__checked');
				break;

			case 1:
				classList.remove('treejs-node__checked');
				classList.add('treejs-node__halfchecked');
				break;

			case 2:
				classList.remove('treejs-node__halfchecked');
				classList.add('treejs-node__checked');
				break;
		}

		switch (node.disabled) {
			case true:
				if (!classList.contains('treejs-node__disabled')) classList.add('treejs-node__disabled');
				break;

			case false:
				if (classList.contains('treejs-node__disabled')) classList.remove('treejs-node__disabled');
				break;
		}
	},

	updateLiElements: function() {
		let self = this;

		Object.values(this.willUpdateNodesById).forEach(function(node) {
			self.updateLiElement(node);
		});
		this.willUpdateNodesById = {};
	},

	markWillUpdateNode: function(node) {
		this.willUpdateNodesById[node.id] = node;
	},

	walkUp: function(node, changeState) {
		var parent = node.parent;

		if (parent) {
			if (changeState === 'status') {
				let pStatus = null;
				let statusCount = parent.children.reduce(function(acc, child) {
					if (!isNaN(child.status)) return acc + child.status;
					return acc;
				}, 0);

				if (statusCount) {
					pStatus = statusCount === parent.children.length * 2 ? 2 : 1;
				} else {
					pStatus = 0;
				}

				if (parent.status === pStatus) return;
				parent.status = pStatus;
			} else {
				let pDisabled = parent.children.reduce(function(acc, child) {
					return acc && child.disabled;
				}, true);
				if (parent.disabled === pDisabled) return;
				parent.disabled = pDisabled;
			}

			this.markWillUpdateNode(parent);
			this.walkUp(parent, changeState);
		}
	},

	walkDown: function(node, changeState) {
		let self = this;

		if (node.children && node.children.length) {
			node.children.forEach(function(child) {
				if (changeState === 'status' && child.disabled) return;
				child[changeState] = node[changeState];

				self.markWillUpdateNode(child);

				self.walkDown(child, changeState);
			});
		}
	},

	/* --- --- --- --- --- Behaviour --- --- --- --- --- --- --- --- --- --- */

	onItemClick: function(id) {
		var node = this.nodesById[id];
		var onChange = this.options.onChange;

		if (!node.disabled) {
			this.setValue(id);
			this.updateLiElements();
		}

		onChange && onChange.call(this);
	},

	onSwitcherClick: function(target) {
		var liEle = target.parentNode;
		var ele = liEle.lastChild;
		var height = ele.scrollHeight;

		if (liEle.classList.contains('treejs-node__close')) {
			_animation(150, {
				enter: function enter() {
					ele.style.height = 0;
					ele.style.opacity = 0;
				},
				active: function active() {
					ele.style.height = "".concat(height, "px");
					ele.style.opacity = 1;
				},
				leave: function leave() {
					ele.style.height = '';
					ele.style.opacity = '';
					liEle.classList.remove('treejs-node__close');
				}
			});
		} else {
			_animation(150, {
				enter: function enter() {
					ele.style.height = "".concat(height, "px");
					ele.style.opacity = 1;
				},
				active: function active() {
					ele.style.height = 0;
					ele.style.opacity = 0;
				},
				leave: function leave() {
					ele.style.height = '';
					ele.style.opacity = '';
					liEle.classList.add('treejs-node__close');
				}
			});
		}
	},

	/* --- --- --- --- --- Export data --- --- --- --- --- ---  ---  ---  --- */

	/* Returns map of half and full selected nodes of tree. */
	getData: function() {
		let data = Object.entries(this.nodesById).reduce(function(data, _ref) {
			let _ref2 = _slicedToArray(_ref, 2);
			let id = _ref2[0];
			let node = _ref2[1];

			if (node.status === 1 || node.status === 2) {
				data.set(id, node.status);
			}

			return data;
		}, new Map());

		return data;
	},
});

/*
Support for selecting the scope and selecting the topics of the scope.
Scopes, Topics are stored the Cookies.
*/
const SCOPES_COOKIE_NAME = 'ElWikiScopes';

var ScopeContentController = {

	tree: null,
	mapScopes: null,

	initialize: function() {
		window.addEvents({
			domready: this.domready.bind(this)
		});
	},

	domready: function() {
		if (!($('idScopeSet')))
			return; // :FVK: workaround - item is missed, due to this is not Scope selection page.
		let self = ScopeContentController;

		self.tree = new STreeViewer('#SoftScopeContainer', {
			closeDepth: 2,
			url: null,
			data: [{
				id: '-1',
				text: 'root',
				children: null,
				checked: false
			}],
			onChange: function() { /* console.log(this.values); */ }
		});

		$('idBtnSetScope').addEvent('click', this.onClickedScopeSet);
		$('idBtnScopeNew').addEvent('click', this.onClickedScopeNew);
		$('idBtnScopeEdit').addEvent('click', this.onClickedScopeEdit);
		$('idBtnScopeRemove').addEvent('click', this.onClickedScopeRemove);
		$('idScopeAreaSwitchAll').addEvent('click', this.onClickedScopeAreaSwitch);
		$('idScopeAreaSwitchSelected').addEvent('click', this.onClickedScopeAreaSwitch);

		$('idBtnScopeTopicsSelected').addEvent('click', this.onClickedTopicsSelect);
		$('idBtnScopeTopicsCancel').addEvent('click', this.onClickedTopicsCancel);
		$('idScopeName').addEvent('input', this.checkEmptyScopeName);


		self.loadScopes();
		self.initialiseSelectScopeDialog();
	},

	/* Gets mapScopes from Cookies. */
	loadScopes: function() {
		let self = ScopeContentController;
		let jsonScopes = $.cookie(SCOPES_COOKIE_NAME);
		if (jsonScopes == undefined) {
			self.mapScopes = new Map();
		} else {
			self.mapScopes = new Map(Object.entries(JSON.parse(jsonScopes)));
		}
	},

	/* Sets mapScopes into Cookies. */
	saveScopes: function() {
		let self = ScopeContentController;
		let dataScope = JSON.stringify(Object.fromEntries(self.mapScopes));
		// :FVK: using path: Wiki.BaseUrl / Wiki.BasePath
		$.cookie(SCOPES_COOKIE_NAME, dataScope, { path: Wiki.BaseUrl, expiry: 123 });
	},

	initialiseSelectScopeDialog: function() {
		let self = ScopeContentController;

		// Populate combobox ScopeList from data (array of string items - are keys of map).
		let elmScopeList = $('idScopeList');
		elmScopeList.empty();
		for (nameScope of [...self.mapScopes.keys()].sort()) {
			elmScopeList.add(new Element('option', { value: nameScope, text: nameScope }), null);
		}

		// If nothing scopes - button SetScope are disabled.
		let elms = document.querySelectorAll('input[name="scopearea"]:checked');
		if (elms.length > 0 && elms[0].get('value') != 'all') {
			$('idBtnSetScope').set('disabled', elmScopeList.options.length == 0);
		}

		// Set status of Edit and Remove buttons of form. Depends on content of combobox 'ScopeList'.
		let btnStatus = ($('idScopeList').length > 0) ? false : true;
		$('idBtnScopeEdit').set('disabled', btnStatus);
		$('idBtnScopeRemove').set('disabled', btnStatus);
	},

	// ---- Scope selection Dialog --- --- --- --- --- --- --- --- --- --- ---

	onClickedScopeAreaSwitch: function(e) {
		console.log(e);
		if (e.target.id == 'idScopeAreaSwitchAll') {
			$('idBtnSetScope').set('disabled', false);
		} else if (e.target.id == 'idScopeAreaSwitchSelected') {
			let select = document.getElementById('idScopeList');
			$('idBtnSetScope').set('disabled', select.options.length == 0);
		}
	},

	/* Event on click button 'Set Scope' */
	onClickedScopeSet: function() {
		let self = ScopeContentController;
		self.saveScopes();

		// Store selected scope name or nothing for 'all' scope.
		let elms = document.querySelectorAll('input[name="scopearea"]:checked');
		if (elms.length > 0) {
			let scope = elms[0].get('value');
			if (scope == 'all') {
				Wiki.prefs('scopearea', '');
			} else {
				// Get selected scope from combobox 'ScopeList'.
				let select = document.getElementById('idScopeList');
				let nameScope = select.options[select.selectedIndex].text; // OR 'value'
				Wiki.prefs('scopearea', nameScope);
			}
		}
		//Wiki.prefs('scopearea', )
	},

	/* Event on click button 'Remove...' */
	onClickedScopeRemove: function() {
		let self = ScopeContentController;

		// Get selected scope name from combobox 'ScopeList'.
		let select = document.getElementById('idScopeList');
		let nameScope = select.options[select.selectedIndex].text; // OR 'value'

		self.mapScopes.delete(nameScope);

		self.initialiseSelectScopeDialog();
	},

	/* Event on click button 'New...' */
	onClickedScopeNew: function() {
		ScopeContentController.initialiseSelectTopicsDialog('new');
	},

	/* Event on click button 'Edit...' */
	onClickedScopeEdit: function() {
		let self = ScopeContentController;

		// Get selected scope from combobox 'ScopeList'.
		let select = document.getElementById('idScopeList');
		let nameScope = select.options[select.selectedIndex].text; // OR 'value'

		// Get selected topics of the chosen scope.
		let selectedTopics = [];
		let jsonTopics;
		if (jsonTopics = self.mapScopes.get(nameScope)) {
			let mapSelectedNodes = new Map(Object.entries(JSON.parse(jsonTopics)));
			for (let [key, value] of mapSelectedNodes) {
				if (value === 2) selectedTopics.push(key);
			}
		}
		self.initialiseSelectTopicsDialog('edit', nameScope, selectedTopics);
	},

	initialiseSelectTopicsDialog: function(mode, previous, selectedTopics) {
		let self = ScopeContentController;

		$('idScopeSet').removeClass('active');
		$('idTopicsSelect').addClass('active');

		let elmScopeName = $('idScopeName');
		elmScopeName.set('data-mode', mode);
		if (previous) {
			elmScopeName.set('value', previous);
			elmScopeName.set('data-previous', previous);
		} else {
			elmScopeName.set('value', '');
		}
		self.checkEmptyScopeName();

		ScopeContentController.tree = new STreeViewer('#SoftScopeContainer', {
			closeDepth: 2,
			url: "/wikiscope/getpages",
			data: [{
				id: '-1',
				text: 'root',
				children: null,
				checked: false
			}],
			values: selectedTopics,
			onChange: function() { /* console.log(this.values); */ }
		});
	},

	// ---- Selection of Scope's Topics Dialog --- --- --- --- --- --- --- ---

	checkEmptyScopeName: function() {
		let nameScope = $('idScopeName').get('value');
		if (!nameScope || nameScope.trim().length == 0) {
			$('idBtnScopeTopicsSelected').set('disabled', true);
		} else {
			$('idBtnScopeTopicsSelected').set('disabled', false);
		}
	},

	/* Event on click button 'Select Topics' - completing selection topics, and editing name of scope.
	 *
	 * Actions:
	 * Remove old scope item from MapScopes, if it exists (via edit mode).
	 * Put new scope item into MapScopes, it contains selected topics.
	 * Save MapScopes into Cookies.  
	 */
	onClickedTopicsSelect: function() {
		self = ScopeContentController;
		$('idTopicsSelect').removeClass('active');
		$('idScopeSet').addClass('active');

		// Remove old scope item from MapScopes.
		let elmScopeName = $('idScopeName');
		let mode = elmScopeName.get('data-mode');
		let previous = elmScopeName.get('data-previous');
		if (mode === 'edit' && previous) self.mapScopes.delete(previous);

		// Put new scope item into MapScopes.
		let nameScope = elmScopeName.get('value').trim();
		let mapSelectedTopics = self.tree.getData();
		let jsonSelectedTopics = JSON.stringify(Object.fromEntries(mapSelectedTopics));
		self.mapScopes.set(nameScope, jsonSelectedTopics);

		self.initialiseSelectScopeDialog();
	},

	/* Event on click button 'Cancel' */
	onClickedTopicsCancel: function() {
		$('idTopicsSelect').removeClass('active');
		$('idScopeSet').addClass('active');

		self.initialiseSelectScopeDialog();
	},
};

ScopeContentController.initialize();
