var ScopeTreeCreator = {

	initialize: function() {
		window.addEvents({
			domready: this.domready.bind(this)
		});
	},

	domready: function() {
		// prettier-ignore
		let data = [{
			"id": "0",
			"text": "node-0",
			"children": [{
				"id": "0-0",
				"text": "node-0-0",
				"children": [{
					"id": "0-0-0",
					"text": "node-0-0-0"
				}, {
					"id": "0-0-1",
					"text": "node-0-0-1"
				}, {
					"id": "0-0-2",
					"text": "node-0-0-2"
				}]
			}, {
				"id": "0-1",
				"text": "node-0-1",
				"children": [{
					"id": "0-1-0",
					"text": "node-0-1-0"
				}, {
					"id": "0-1-1",
					"text": "node-0-1-1"
				}, {
					"id": "0-1-2",
					"text": "node-0-1-2"
				}]
			}]
		}]

		let tree = new Tree('.SoftScopeContainer', {
			url: '/ajax/wikiscope',
			method: 'GET',
/*
			data: [{
				id: '-1',
				text: 'root',
				children: data,
				checked: false
			}],
*/
			closeDepth: 2,
			loaded: function() {
				this.values = ['0-0-0', '0-1-1'];
				console.log(this.selectedNodes);
				console.log(this.values);
				this.disables = ['0-0-0', '0-0-1', '0-0-2']
			},
			onChange: function() {
				console.log(this.values);
			}
		})
		// Wiki.jsonrpc("/pageshierarchyTracker/pages", ["fvk_value", 16], ScopeTreeCreator.callback);
	},

	callback: function(result) {
		var root = new TreeScopeNode("Pages Container");
		for (let item of result) {
			if (typeof item == "object") {
				ScopeTreeCreator.parseJsonObject(item, root);
			}
		}
		new TreeScopeView(root, "#SoftScopeContainerXX", { show_root: false });
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
