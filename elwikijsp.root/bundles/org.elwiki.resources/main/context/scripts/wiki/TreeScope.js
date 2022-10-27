/**
* TreeJS is a JavaScript librarн for displaying TreeViews on the web.
*
* @author Matthias Thalmann (Tree.js)
* @author Victor Fedorov (porting using MooTools)
*/

/**
 * DOM Structure
 * (start code)
 *  ul
 *    li
 *      span.tj_description
 *        span.tj_mod_icon
 *        span.tj_icon
 * (end)
 */

var TreeConfig2 = {
	context_menu: undefined
};

TreeUtil2 = new Class({

	isDOM: function(obj) {
		try {
			return obj instanceof HTMLElement;
		}
		catch (e) {
			return (typeof obj === "object") &&
				(obj.nodeType === 1) && (typeof obj.style === "object") &&
				(typeof obj.ownerDocument === "object");
		}
	},

	expandNode: function(node) {
		node.setExpanded(true);

		if (!node.isLeaf()) {
			node.getChildren().forEach(function(child) {
				expandNode(child);
			});
		}
	},

	collapseNode: function(node) {
		node.setExpanded(false);

		if (!node.isLeaf()) {
			node.getChildren().forEach(function(child) {
				collapseNode(child);
			});
		}
	},

	getSelectedNodesForNode: function(node) {
		if (!instanceOf(node, TreeScopeNode)) {
			throw new Error("Parameter 1 must be of type TreeScopeNode");
		}

		var self = this;
		var ret = new Array();

		if (node.isSelected()) {
			ret.push(node);
		}

		node.getChildren().forEach(function(child) {
			if (child.isSelected()) {
				if (ret.indexOf(child) == -1) {
					ret.push(child);
				}
			}

			if (!child.isLeaf()) {
				self.getSelectedNodesForNode(child).forEach(function(_node) {
					if (ret.indexOf(_node) == -1) {
						ret.push(_node);
					}
				});
			}
		});

		return ret;
	}
});

/**
 * Pointer of this TreeScopeViewBase implementation stores in the root element of DOM's tree container.
 */
TreeScopeViewBase = new Class({

	Implements: [TreeUtil2],

	/* 
	*/
	populateNode: function(node) {
		var self = this;
		var li_outer = document.createElement("li");
		var span_desc = document.createElement("span");
		span_desc.className = "tj_description";
		span_desc.tj_node = node;

		if (!node.isEnabled()) {
			li_outer.setAttribute("disabled", "");
			node.setExpanded(false);
			node.setSelected(false);
		}

		if (node.isSelected()) {
			span_desc.classList.add("selected");
		}

		li_outer.innerHTML = node.toString();

		if (node.isLeaf() && !node.options.forceParent) {
			span_desc.classList.add("tj_leaf");
		} else {
			var button = "button.collapse-btn".slick();
			button.disabled = false;
			button.tj_node = node;
			button.setAttribute("aria-expanded", node.isExpanded());
			li_outer.insertBefore(button, li_outer.firstChild);

			if (node.isExpanded()) {
				var ul_container = document.createElement("ul");
				node.getChildren().forEach(function(child) {
					ul_container.appendChild(self.populateNode(child));
				});
				li_outer.appendChild(ul_container)
			}

			button.addEventListener("click", function(e) {
				var domElement = e.target;
				var treeViewer = null;
				do { // navigating through elements  up to the container of this tree.
					if (typeof domElement.tj_treeViewer !== "undefined") {
						treeViewer = domElement.tj_treeViewer;
						break;
					}
					domElement = domElement.parentElement;
				} while (domElement);
				if (domElement == null) {
					console.warn("No TreeScopeViewBase founded.");
					return;
				}

				cur_el = e.target;
				while (typeof cur_el.tj_node === "undefined" || cur_el.classList.contains("tj_container")) {
					cur_el = cur_el.parentElement;
				}
				var node_cur = cur_el.tj_node;

				if (typeof node_cur === "undefined") {
					return;
				}

				if (node_cur.isEnabled()) {
					if (e.ctrlKey == false) {
						if (!node_cur.isLeaf()) {
							node_cur.toggleExpanded();
							treeViewer.reload();
						} else {
							node_cur.open();
						}

						node_cur.on("click")(e, node_cur);
					}

					if (e.ctrlKey == true) {
						node_cur.toggleSelected();
						treeViewer.reload();
					} else {
						var rt = node_cur.getRoot();

						if (instanceOf(rt, TreeScopeNode)) {
							treeViewer.getSelectedNodesForNode(rt).forEach(function(_nd) {
								_nd.setSelected(false);
							});
						}
						node_cur.setSelected(true);

						treeViewer.reload();
					}
				}
			});
		}

		span_desc.addEventListener("contextmenu", function(e) {
			var cur_el = e.target;

			while (typeof cur_el.tj_node === "undefined" || cur_el.classList.contains("tj_container")) {
				cur_el = cur_el.parentElement;
			}

			var node_cur = cur_el.tj_node;

			if (typeof node_cur === "undefined") {
				return;
			}

			if (typeof node_cur.getListener("contextmenu") !== "undefined") {
				node_cur.on("contextmenu")(e, node_cur);
				e.preventDefault();
			} else if (typeof TreeConfig2.context_menu === "function") {
				TreeConfig2.context_menu(e, node_cur);
				e.preventDefault();
			}
		});

		return li_outer;
	},

	/**
	 * Reloads/Renders the tree inside of the container.
	 * Always called from this object.
	 */
	reload: function() {
		if (this.container == null) {
			console.warn("No container specified");
			return;
		}

		this.container.classList.add("tj_container");
		this.container.tj_treeViewer = this;

		var cnt = document.createElement("ul");

		if (this.options.show_root) {
			cnt.appendChild(this.populateNode(this.root));
		} else {
			this.root.getChildren().forEach(function(child) {
				cnt.appendChild(this.populateNode(child));
			}.bind(this));
		}

		this.container.innerHTML = "";
		this.container.appendChild(cnt);
	},

});

TreeScopeView = new Class({
	Implements: [TreeScopeViewBase, Options],

	root: null,
	container: null,

	options: {
		show_root: true,
		leaf_icon: "&#128441;",
		parent_icon: "&#128449;",
		open_icon: "&#9698;",
		close_icon: "&#9654;",
	},

	/*
	* Konstruktor.
	*/
	initialize: function(_root, _container, options) {
		if (typeof _root === "undefined") {
			throw new Error("Parameter 1 must be set (root)");
		}

		if (!instanceOf(_root, TreeScopeNode)) {
			throw new Error("Parameter 1 must be of type TreeScopeNode");
		}
		this.root = _root;
		this.setOptions(options);

		if (_container) {
			if (!this.isDOM(_container)) {
				this.container = document.querySelector(_container);

				if (this.container instanceof Array) {
					this.container = this.container[0];
				}

				if (!this.isDOM(this.container)) {
					throw new Error("Parameter 2 must be either DOM-Object or CSS-QuerySelector (#, .)");
				}
			}
		} else {
			this.container = null;
		}

		// initialize view.
		if (typeof this.container !== "undefined") {
			this.reload();
		}
	},

	/* Resets the root-node (TreeScopeNode).
	*/
	setRoot: function(_root) {
		if (instanceOf(this.root, TreeScopeNode)) {
			this.root = _root;
		}
	},

	/* Returns the root-node.
	*/
	getRoot: function() {
		return this.root;
	},

	/* Expands all nodes of the tree.
	*/
	expandAllNodes: function() {
		this.root.setExpanded(true);

		this.root.getChildren().forEach(function(child) {
			this.expandNode(child);
		});
	},

	/* Expands all nodes that are in the path (TreeScopePath).
	*/
	expandPath: function(path) {
		if (!(instanceOf(path, TreeScopePath))) {
			throw new Error("Parameter 1 must be of type TreeScopePath");
		}

		path.getPath().forEach(function(node) {
			node.setExpanded(true);
		});
	},

	/*  Collapses all nodes of the tree.
	*/
	collapseAllNodes: function() {
		this.root.setExpanded(false);

		this.root.getChildren().forEach(function(child) {
			this.collapseNode(child);
		});
	},

	/* Resets the container (DOM-Element/querySelector).
	*/
	setContainer: function(_container) {
		if (this.isDOM(_container)) {
			this.container = _container;
		} else {
			_container = document.querySelector(_container);

			if (_container instanceof Array) {
				_container = _container[0];
			}

			if (!this.isDOM(_container)) {
				throw new Error("Parameter 1 must be either DOM-Object or CSS-QuerySelector (#, .)");
			}
		}
	},

	/* Returns the container.
	*/
	getContainer: function() {
		return this.container;
	},

	/* Returns all selected nodes in the tree.
	*/
	// TODO: set selected key: up down; expand right; collapse left; enter: open;
	getSelectedNodes: function() {
		return this.getSelectedNodesForNode(this.root);
	}
});

TreeScopeNode = new Class({
	Implements: [Options],

	userObject: "", // default label of node.

	children: new Array(),
	events: new Array(),

	options: { // default options.
		expanded: true,
		enabled: true,
		selected: false,
		allowsChildren: true,

		parent: null,

		icon: "",
		forceParent: false,
	},

	/*
	* Konstruktor.
	*/
	initialize: function(_userObject, options) {
		this.setOptions(options);

		if (_userObject) {
			if (typeof _userObject !== "string" && typeof _userObject.toString !== "function") {
				throw new Error("Parameter 1 must be of type String or Object, where it must have the function toString()");
			}
			this.userObject = _userObject;
		}
	},

	addChild: function(node) {
		if (!this.options.allowsChildren) {
			console.warn("Option allowsChildren is set to false, no child added");
			return;
		}

		if (instanceOf(node, TreeScopeNode)) {
			this.children.push(node);
			node.options.parent = this;
		} else {
			throw new Error("Parameter 1 must be of type TreeScopeNode");
		}
	},

	removeChildPos: function(pos) {
		if (typeof this.children[pos] !== "undefined") {
			if (typeof this.children[pos] !== "undefined") {
				this.children.splice(pos, 1);
			}
		}
	},

	removeChild: function(node) {
		if (!instanceOf(node, TreeScopeNode)) {
			throw new Error("Parameter 1 must be of type TreeScopeNode");
		}

		this.removeChildPos(this.getIndexOfChild(node));
	},

	getChildren: function() {
		return this.children;
	},

	getChildCount: function() {
		return this.children.length;
	},

	getIndexOfChild: function(node) {
		for (var i = 0; i < this.children.length; i++) {
			if (this.children[i].equals(node)) {
				return i;
			}
		}

		return -1;
	},

	getRoot: function() {
		var node = this;

		while (node.options.parent != null) {
			node = node.options.parent;
		}

		return node;
	},

	setUserObject: function(_userObject) {
		if (!(typeof _userObject === "string") || typeof _userObject.toString !== "function") {
			throw new Error("Parameter 1 must be of type String or Object, where it must have the function toString()");
		} else {
			this.userObject = _userObject;
		}
	},

	getUserObject: function() {
		return this.userObject;
	},

	isLeaf: function() {
		return (this.children.length == 0);
	},

	setExpanded: function(_expanded) {
		if (this.isLeaf()) {
			return;
		}

		if (typeof _expanded === "boolean") {
			if (this.options.expanded == _expanded) {
				return;
			}

			this.options.expanded = _expanded;

			if (_expanded) {
				this.on("expand")(this);
			} else {
				this.on("collapse")(this);
			}

			this.on("toggle_expanded")(this);
		}
	},

	toggleExpanded: function() {
		(this.options.expanded) ?
			this.setExpanded(false) :
			this.setExpanded(true);
	},

	isExpanded: function() {
		if (this.isLeaf()) {
			return true;
		} else {
			return this.options.expanded;
		}
	},

	setEnabled: function(_enabled) {
		if (typeof _enabled === "boolean") {
			if (this.options.enabled == _enabled) {
				return;
			}

			this.options.enabled = _enabled;

			if (_enabled) {
				this.on("enable")(this);
			} else {
				this.on("disable")(this);
			}

			this.on("toggle_enabled")(this);
		}
	},

	toggleEnabled: function() {
		if (this.options.enabled) {
			this.setEnabled(false);
		} else {
			this.setEnabled(true);
		}
	},

	isEnabled: function() {
		return this.options.enabled;
	},

	setSelected: function(_selected) {
		if (typeof _selected !== "boolean") {
			return;
		}

		if (this.options.selected == _selected) {
			return;
		}

		this.options.selected = _selected;

		if (_selected) {
			this.on("select")(this);
		} else {
			this.on("deselect")(this);
		}

		this.on("toggle_selected")(this);
	},

	toggleSelected: function() {
		if (this.options.selected) {
			this.setSelected(false);
		} else {
			this.setSelected(true);
		}
	},

	isSelected: function() {
		return this.options.selected;
	},

	open: function() {
		if (!this.isLeaf()) {
			this.on("open")(this);
		}
	},

	on: function(ev, callback) {
		if (typeof callback === "undefined") {
			if (typeof this.events[ev] !== "function") {
				return function() { };
			} else {
				return this.events[ev];
			}
		}

		if (typeof callback !== 'function') {
			throw new Error("Argument 2 must be of type function");
		}

		this.events[ev] = callback;
	},

	getListener: function(ev) {
		return this.events[ev];
	},

	equals: function(node) {
		if (instanceOf(node, TreeScopeNode)) {
			if (node.getUserObject() == this.userObject) {
				return true;
			}
		}

		return false;
	},

	toString: function() {
		if (typeof this.userObject === "string") {
			return this.userObject;
		} else {
			return this.userObject.toString();
		}
	}
});

TreeScopePath = new Class({
	//function TreeScopePath(root, node) {
	nodes: new Array(),

	initialize: function(root, node) {
		if (root instanceof TreeScopeNode && node instanceof TreeScopeNode) {
			this.setPath(root, node);
		}
	},

	setPath: function(root, node) {
		nodes = new Array();

		while (typeof node !== "undefined" && !node.equals(root)) {
			nodes.push(node);
			node = node.parent;
		}

		if (node.equals(root)) {
			nodes.push(root);
		} else {
			nodes = new Array();
			throw new Error("Node is not contained in the tree of root");
		}

		nodes = nodes.reverse();

		return nodes;
	},

	getPath: function() {
		return nodes;
	},

	toString: function() {
		return nodes.join(" - ");
	}
});

var ScopeTreeCreator = {

	initialize: function() {
		window.addEvents({
			domready: this.domready.bind(this)
		});
	},

	domready: function() {
		Wiki.jsonrpc("/pageshierarchyTracker/pages", ["fvk_value", 16], ScopeTreeCreator.callback);
	},

	callback: function(result) {
		var root = new TreeScopeNode("Pages Container");
		for (let item of result) {
			if (typeof item == "object") {
				ScopeTreeCreator.parseJsonObject(item, root);
			}
		}
		new TreeScopeView(root, "#SoftScopeContainerXX", {show_root: false});
	},

	parseJsonObject: function(obj, node) {
		let childNode = new TreeScopeNode(obj.name);
		node.addChild(childNode);
		if (obj.hasOwnProperty('children')) { // && Array.isArray(obj.children)
			for (let item of obj.children) {
				ScopeTreeCreator.parseJsonObject(item, childNode);
			}
		}
	},

};

ScopeTreeCreator.initialize();
