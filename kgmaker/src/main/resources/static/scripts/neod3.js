'use strict';
(function(){
var __hasProp = {}.hasOwnProperty;

window.neo = {};

neo.models = {};

neo.renderers = {
  node: [],
  relationship: []
};

neo.utils = {
  copy: function(src) {
    return JSON.parse(JSON.stringify(src));
  },
  extend: function(dest, src) {
    var k, v;
    if (!neo.utils.isObject(dest) && neo.utils.isObject(src)) {
      return;
    }
    for (k in src) {
      if (!__hasProp.call(src, k)) continue;
      v = src[k];
      dest[k] = v;
    }
    return dest;
  },
  isArray: Array.isArray || function(obj) {
    return Object.prototype.toString.call(obj) === '[object Array]';
  },
  isObject: function(obj) {
    return Object(obj) === obj;
  }
};

var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; },
  __hasProp = {}.hasOwnProperty,
  __slice = [].slice;

neo.models.Graph = (function() {
  function Graph(cypher) {
    this.removeRelationships = __bind(this.removeRelationships, this);
    this.removeNodes = __bind(this.removeNodes, this);
    this.findRelationship = __bind(this.findRelationship, this);
    this.findNode = __bind(this.findNode, this);
    this.addRelationships = __bind(this.addRelationships, this);
    this.addNodes = __bind(this.addNodes, this);
    this.nodeMap = {};
    this.relationshipMap = {};
    if (cypher) {
      this.addNodes(cypher.nodes);
      this.addRelationships(cypher.relationships);
    }
  }

  Graph.prototype.nodes = function() {
    var key, value, _ref, _results;
    _ref = this.nodeMap;
    _results = [];
    for (key in _ref) {
      if (!__hasProp.call(_ref, key)) continue;
      value = _ref[key];
      _results.push(value);
    }
    return _results;
  };

  Graph.prototype.relationships = function() {
    var key, value, _ref, _results;
    _ref = this.relationshipMap;
    _results = [];
    for (key in _ref) {
      if (!__hasProp.call(_ref, key)) continue;
      value = _ref[key];
      _results.push(value);
    }
    return _results;
  };

  Graph.prototype.addNodes = function(item) {
    var items, node, _base, _i, _len, _name;
    items = !neo.utils.isArray(item) ? [item] : item;
    for (_i = 0, _len = items.length; _i < _len; _i++) {
      item = items[_i];
      node = !(item instanceof neo.models.Node) ? new neo.models.Node(item.id, item.labels, item.properties) : item;
      (_base = this.nodeMap)[_name = item.id] || (_base[_name] = node);
    }
    return this;
  };
  Graph.prototype.addRelationships = function(item) {
    var items, source, target, _i, _len;
    items = !neo.utils.isArray(item) ? [item] : item;
    for (_i = 0, _len = items.length; _i < _len; _i++) {
      item = items[_i];
      source = this.nodeMap[item.source] || (function() {
        throw "Invalid source";
      })();
      target = this.nodeMap[item.target] || (function() {
        throw "Invalid target";
      })();
      this.relationshipMap[item.id] = new neo.models.Relationship(item.id, source, target, item.type, item.properties);
    }
    return this;
  };

  Graph.prototype.findNode = function(id) {
    return this.nodeMap[id];
  };

  Graph.prototype.findRelationship = function(id) {
    return this.relationshipMap[id];
  };

  Graph.prototype.merge = function(result) {
    this.addNodes(result.nodes);
    this.addRelationships(result.relationships);
    return this;
  };

  Graph.prototype.removeNodes = function() {
    var id, rId, rel, rels, remove, _i, _len;
    remove = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
    if (arguments.length === 0) {
      this.nodeMap = {};
      this.relationshipMap = {};
      return this;
    }
    remove = neo.utils.isArray(remove[0]) ? remove[0] : remove;
    for (_i = 0, _len = remove.length; _i < _len; _i++) {
      id = remove[_i];
      rels = (function() {
        var _ref, _results;
        _ref = this.relationshipMap;
        _results = [];
        for (rId in _ref) {
          if (!__hasProp.call(_ref, rId)) continue;
          rel = _ref[rId];
          if (rel.source.id === id || rel.target.id === id) {
            _results.push(rel.id);
          }
        }
        return _results;
      }).call(this);
      this.removeRelationships(rels);
      delete this.nodeMap[id];
    }
    return this;
  };

  Graph.prototype.removeRelationships = function() {
    var id, remove, _i, _len;
    remove = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
    if (arguments.length === 0) {
      this.relationshipMap = {};
      return this;
    }
    remove = neo.utils.isArray(remove[0]) ? remove[0] : remove;
    for (_i = 0, _len = remove.length; _i < _len; _i++) {
      id = remove[_i];
      delete this.relationshipMap[id];
    }
    return this;
  };

  return Graph;

})();

var NeoD3Geometry;

NeoD3Geometry = (function() {
  var addShortenedNextWord, fitCaptionIntoCircle, noEmptyLines, square;

  square = function(distance) {
    return distance * distance;
  };

  function NeoD3Geometry(style) {
    this.style = style;
  }

  addShortenedNextWord = function(line, word, measure) {
    var _results;
    _results = [];
    while (!(word.length <= 2)) {
      word = word.substr(0, word.length - 2) + '\u2026';
      if (measure(word) < line.remainingWidth) {
        line.text += " " + word;
        break;
      } else {
        _results.push(void 0);
      }
    }
    return _results;
  };

  noEmptyLines = function(lines) {
    var line, _i, _len;
    for (_i = 0, _len = lines.length; _i < _len; _i++) {
      line = lines[_i];
      if (line.text.length === 0) {
        return false;
      }
    }
    return true;
  };

  NeoD3Geometry.prototype.formatNodeCaptions = function(nodes) {
    var captionText, i, lines, node, template, words, _i, _j, _len, _ref, _results;
    var style = this.style;
    _results = [];
    for (_i = 0, _len = nodes.length; _i < _len; _i++) {
      node = nodes[_i];
      template = style.forNode(node).get("caption");
      captionText = style.interpolate(template, node.id, node.propertyMap);
      words = captionText.split(" ");
      lines = [];
      for (i = _j = 0, _ref = words.length - 1; 0 <= _ref ? _j <= _ref : _j >= _ref; i = 0 <= _ref ? ++_j : --_j) {
        lines.push({
          node: node,
          text: words[i],
          baseline: (1 + i - words.length / 2) * 10
        });
      }
      _results.push(node.caption = lines);
    }
    return _results;
  };
  
  fitCaptionIntoCircle = function(node, style) {
    var candidateLines, candidateWords, captionText, consumedWords, emptyLine, fitOnFixedNumberOfLines, fontFamily, fontSize, lineCount, lineHeight, lines, maxLines, measure, template, words, _i, _ref, _ref1;
    template = style.forNode(node).get("caption");
    captionText = style.interpolate(template, node.id, node.propertyMap);
    fontFamily = 'sans-serif';
    fontSize = parseFloat(style.forNode(node).get('font-size'));
    lineHeight = fontSize;
    measure = function(text) {
      return neo.utils.measureText(text, fontFamily, fontSize);
    };
    words = captionText.split(" ");
    emptyLine = function(lineCount, iLine) {
      var baseline, constainingHeight, lineWidth;
      baseline = (1 + iLine - lineCount / 2) * lineHeight;
      constainingHeight = iLine < lineCount / 2 ? baseline - lineHeight : baseline;
      lineWidth = Math.sqrt(square(node.radius) - square(constainingHeight)) * 2;
      return {
        node: node,
        text: '',
        baseline: baseline,
        remainingWidth: lineWidth
      };
    };
    fitOnFixedNumberOfLines = function(lineCount) {
      var iLine, iWord, line, lines, _i, _ref;
      lines = [];
      iWord = 0;
      for (iLine = _i = 0, _ref = lineCount - 1; 0 <= _ref ? _i <= _ref : _i >= _ref; iLine = 0 <= _ref ? ++_i : --_i) {
        line = emptyLine(lineCount, iLine);
        while (iWord < words.length && measure(" " + words[iWord]) < line.remainingWidth) {
          line.text += " " + words[iWord];
          line.remainingWidth -= measure(" " + words[iWord]);
          iWord++;
        }
        lines.push(line);
      }
      if (iWord < words.length) {
        addShortenedNextWord(lines[lineCount - 1], words[iWord], measure);
      }
      return [lines, iWord];
    };
    consumedWords = 0;
    maxLines = node.radius * 2 / fontSize;
    lines = [emptyLine(1, 0)];
    for (lineCount = _i = 1; 1 <= maxLines ? _i <= maxLines : _i >= maxLines; lineCount = 1 <= maxLines ? ++_i : --_i) {
      _ref = fitOnFixedNumberOfLines(lineCount), candidateLines = _ref[0], candidateWords = _ref[1];
      if (noEmptyLines(candidateLines)) {
        _ref1 = [candidateLines, candidateWords], lines = _ref1[0], consumedWords = _ref1[1];
      }
      if (consumedWords >= words.length) {
        return lines;
      }
    }
    return lines;
  };

  // NeoD3Geometry.prototype.formatNodeCaptions = function(nodes) {
  //   var node, _i, _len, _results;
  //   _results = [];
  //   for (_i = 0, _len = nodes.length; _i < _len; _i++) {
  //     node = nodes[_i];
  //     _results.push(node.caption = fitCaptionIntoCircle(node, this.style));
  //   }
  //   return _results;
  // };

  NeoD3Geometry.prototype.measureRelationshipCaption = function(relationship, caption) {
    var fontFamily, fontSize, padding;
    fontFamily = 'sans-serif';
    fontSize = parseFloat(this.style.forRelationship(relationship).get('font-size'));
    padding = parseFloat(this.style.forRelationship(relationship).get('padding'));
    return neo.utils.measureText(caption, fontFamily, fontSize) + padding * 2;
  };

  NeoD3Geometry.prototype.captionFitsInsideArrowShaftWidth = function(relationship) {
    return parseFloat(this.style.forRelationship(relationship).get('shaft-width')) > parseFloat(this.style.forRelationship(relationship).get('font-size'));
  };

  NeoD3Geometry.prototype.measureRelationshipCaptions = function(relationships) {
    var relationship, _i, _len, _results;
    _results = [];
    for (_i = 0, _len = relationships.length; _i < _len; _i++) {
      relationship = relationships[_i];
      relationship.captionLength = this.measureRelationshipCaption(relationship, relationship.type);
      _results.push(relationship.captionLayout = this.captionFitsInsideArrowShaftWidth(relationship) ? "internal" : "external");
    }
    return _results;
  };

  NeoD3Geometry.prototype.shortenCaption = function(relationship, caption, targetWidth) {
    var shortCaption, width;
    shortCaption = caption;
    while (true) {
      if (shortCaption.length <= 2) {
        return ['', 0];
      }
      shortCaption = shortCaption.substr(0, shortCaption.length - 2) + '\u2026';
      width = this.measureRelationshipCaption(relationship, shortCaption);
      if (width < targetWidth) {
        return [shortCaption, width];
      }
    }
  };

  NeoD3Geometry.prototype.layoutRelationships = function(relationships) {
    var alongPath, dx, dy, endBreak, headHeight, headRadius, length, relationship, shaftLength, shaftRadius, startBreak, _i, _len, _ref, _results;
    _results = [];
    for (_i = 0, _len = relationships.length; _i < _len; _i++) {
      relationship = relationships[_i];
      dx = relationship.target.x - relationship.source.x;
      dy = relationship.target.y - relationship.source.y;
      length = Math.sqrt(square(dx) + square(dy));
      relationship.arrowLength = length - relationship.source.radius - relationship.target.radius;
      alongPath = function(from, distance) {
        return {
          x: from.x + dx * distance / length,
          y: from.y + dy * distance / length
        };
      };
      shaftRadius = (parseFloat(this.style.forRelationship(relationship).get('shaft-width')) / 2) || 2;
      headRadius = shaftRadius + 3;
      headHeight = headRadius * 2;
      shaftLength = relationship.arrowLength - headHeight;
      relationship.startPoint = alongPath(relationship.source, relationship.source.radius);
      relationship.endPoint = alongPath(relationship.target, -relationship.target.radius);
      relationship.midShaftPoint = alongPath(relationship.startPoint, shaftLength / 2);
      relationship.angle = Math.atan2(dy, dx) / Math.PI * 180;
      relationship.textAngle = relationship.angle;
      if (relationship.angle < -90 || relationship.angle > 90) {
        relationship.textAngle += 180;
      }
      _ref = shaftLength > relationship.captionLength ? [relationship.type, relationship.captionLength] : this.shortenCaption(relationship, relationship.type, shaftLength), relationship.shortCaption = _ref[0], relationship.shortCaptionLength = _ref[1];
      if (relationship.captionLayout === "external") {
        startBreak = (shaftLength - relationship.shortCaptionLength) / 2;
        endBreak = shaftLength - startBreak;
        _results.push(relationship.arrowOutline = ['M', 0, shaftRadius, 'L', startBreak, shaftRadius, 'L', startBreak, -shaftRadius, 'L', 0, -shaftRadius, 'Z', 'M', endBreak, shaftRadius, 'L', shaftLength, shaftRadius, 'L', shaftLength, headRadius, 'L', relationship.arrowLength, 0, 'L', shaftLength, -headRadius, 'L', shaftLength, -shaftRadius, 'L', endBreak, -shaftRadius, 'Z'].join(' '));
      } else {
        _results.push(relationship.arrowOutline = ['M', 0, shaftRadius, 'L', shaftLength, shaftRadius, 'L', shaftLength, headRadius, 'L', relationship.arrowLength, 0, 'L', shaftLength, -headRadius, 'L', shaftLength, -shaftRadius, 'L', 0, -shaftRadius, 'Z'].join(' '));
      }
    }
    return _results;
  };

  NeoD3Geometry.prototype.setNodeRadii = function(nodes) {
    var node, _i, _len, _results;
    _results = [];
    for (_i = 0, _len = nodes.length; _i < _len; _i++) {
      node = nodes[_i];
      _results.push(node.radius = parseFloat(this.style.forNode(node).get("diameter")) / 2);
    }
    return _results;
  };

  NeoD3Geometry.prototype.onGraphChange = function(graph) {
    this.setNodeRadii(graph.nodes());
    this.formatNodeCaptions(graph.nodes());
    return this.measureRelationshipCaptions(graph.relationships());
  };

  NeoD3Geometry.prototype.onTick = function(graph) {
    return this.layoutRelationships(graph.relationships());
  };

  return NeoD3Geometry;

})();

var __slice = [].slice;

neo.graphModel = function() {
  var graph, model;
  graph = new neo.models.Graph();
  model = function() {};
  model.callbacks = {};
  model.trigger = function() {
    var args, callback, event, _i, _len, _ref, _results;
    event = arguments[0], args = 2 <= arguments.length ? __slice.call(arguments, 1) : [];
    event = 'updated';
    if (model.callbacks[event]) {
      _ref = model.callbacks[event];
      _results = [];
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        callback = _ref[_i];
        _results.push(callback.apply(null, args));
      }
      return _results;
    }
  };
  model.nodes = function(items) {
    if (items == null) {
      return graph.nodes();
    }
    graph.removeNodes().addNodes(items);
    model.trigger('nodesAdded');
    return model;
  };
  model.nodes.add = function(items) {
    if (items != null) {
      graph.addNodes(items);
      model.trigger('nodesAdded');
    }
    return model;
  };
  model.nodes.find = graph.findNode;
  model.nodes.remove = function() {
    graph.removeNodes.apply(null, arguments);
    model.trigger('nodesRemoved');
    return model;
  };
  model.relationships = function(items) {
    if (items == null) {
      return graph.relationships();
    }
    graph.removeRelationships().addRelationships(items);
    model.trigger('relationshipsAdded');
    return model;
  };
  model.relationships.add = function(items) {
    if (items != null) {
      graph.addRelationships(items);
      model.trigger('relationshipsAdded');
    }
    return model;
  };
  model.relationships.find = graph.findRelationship;
  model.relationships.remove = function() {
    graph.removeRelationships.apply(null, arguments);
    model.trigger('relationshipsRemoved');
    return model;
  };
  model.on = function(event, callback) {
    var _base;
    ((_base = model.callbacks)[event] != null ? _base[event] : _base[event] = []).push(callback);
    return model;
  };
  return model;
};

var __slice = [].slice;

neo.graphView = function() {
  var callbacks, chart, layout, style, trigger, viz;
  layout = neo.layout.force();
  style = neo.style();
  viz = null;
  callbacks = {};
  trigger = function() {
    var args, callback, event, _i, _len, _ref, _results;
    event = arguments[0], args = 2 <= arguments.length ? __slice.call(arguments, 1) : [];
    _ref = callbacks[event];
    _results = [];
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      callback = _ref[_i];
      _results.push(callback.apply(null, args));
    }
    return _results;
  };
  chart = function(selection) {
    selection.each(function(graphModel) {
      if (!viz) {
        viz = neo.viz(this, graphModel, layout, style);
        graphModel.on('updated', function() {
          return viz.update();
        });
        viz.trigger = trigger;
      }
      return viz.update();
    });
  };
  chart.on = function(event, callback) {
    (callbacks[event] != null ? callbacks[event] : callbacks[event] = []).push(callback);
    return chart;
  };
  chart.layout = function(value) {
    if (!arguments.length) {
      return layout;
    }
    layout = value;
    return chart;
  };
  chart.style = function(value) {
    if (!arguments.length) {
      return style.toSheet();
    }
    style.importGrass(value);
    return chart;
  };
  chart.width = function(value) {
    if (!arguments.length) {
      return viz.width;
    }
    return chart;
  };
  chart.height = function(value) {
    if (!arguments.length) {
      return viz.height;
    }
    return chart;
  };
  chart.update = function() {
    viz.update();
    return chart;
  };
  return chart;
};

neo.layout = (function() {
  var _layout;
  _layout = {};
  _layout.force = function() {
    var _force;
    _force = {};
    _force.init = function(render) {
      var accelerateLayout, d3force, forceLayout, linkDistance;
      forceLayout = {};
      linkDistance = 60;
      d3force = d3.layout.force().linkDistance(linkDistance).charge(-1000).gravity(0.3);
      accelerateLayout = function() {
        var d3Tick, maxAnimationFramesPerSecond, maxComputeTime, maxStepsPerTick, now;
        maxStepsPerTick = 100;
        maxAnimationFramesPerSecond = 60;
        maxComputeTime = 1000 / maxAnimationFramesPerSecond;
        now = window.performance ? function() {
          return window.performance.now();
        } : function() {
          return Date.now();
        };
        d3Tick = d3force.tick;
        return d3force.tick = (function(_this) {
          return function() {
            var startTick, step;
            startTick = now();
            step = maxStepsPerTick;
            while (step-- && now() - startTick < maxComputeTime) {
              if (d3Tick()) {
                maxStepsPerTick = 2;
                return true;
              }
            }
            render();
            return false;
          };
        })(this);
      };
      accelerateLayout();
      forceLayout.update = function(graph, size) {
        var center, nodes, radius, relationships;
        nodes = graph.nodes();
        relationships = graph.relationships();
        radius = nodes.length * linkDistance / (Math.PI * 2);
        center = {
          x: size[0] / 2,
          y: size[1] / 2
        };
        neo.utils.circularLayout(nodes, center, radius);
        return d3force.nodes(nodes).links(relationships).size(size).start();
      };
      forceLayout.drag = d3force.drag;
      return forceLayout;
    };
    return _force;
  };
  return _layout;
})();

var __hasProp = {}.hasOwnProperty;

neo.models.Node = (function() {
  function Node(id, labels, properties) {
    var key, value;
    this.id = id;
    this.labels = labels;
    this.propertyMap = properties;
    this.propertyList = (function() {
      var _results;
      _results = [];
      for (key in properties) {
        if (!__hasProp.call(properties, key)) continue;
        value = properties[key];
        _results.push({
          key: key,
          value: value
        });
      }
      return _results;
    })();
  }

  Node.prototype.toJSON = function() {
    return this.propertyMap;
  };

  Node.prototype.isNode = true;

  Node.prototype.isRelationship = false;

  return Node;

})();

var __hasProp = {}.hasOwnProperty;

neo.models.Relationship = (function() {
  function Relationship(id, source, target, type, properties) {
    var key, value;
    this.id = id;
    this.source = source;
    this.target = target;
    this.type = type;
    this.propertyMap = properties;
    this.propertyList = (function() {
      var _ref, _results;
      _ref = this.propertyMap;
      _results = [];
      for (key in _ref) {
        if (!__hasProp.call(_ref, key)) continue;
        value = _ref[key];
        _results.push({
          key: key,
          value: value
        });
      }
      return _results;
    }).call(this);
  }

  Relationship.prototype.toJSON = function() {
    return this.propertyMap;
  };

  Relationship.prototype.isNode = false;

  Relationship.prototype.isRelationship = true;

  return Relationship;

})();

neo.Renderer = (function() {
  function Renderer(opts) {
    if (opts == null) {
      opts = {};
    }
    neo.utils.extend(this, opts);
    if (this.onGraphChange == null) {
      this.onGraphChange = function() {};
    }
    if (this.onTick == null) {
      this.onTick = function() {};
    }
  }

  return Renderer;

})();

neo.style = (function() {
  var GraphStyle, Selector, StyleElement, StyleRule, _style;
  _style = function(storage) {
    return new GraphStyle(storage);
  };
  _style.defaults = {
    autoColor: true,
    colors: [
      {
        color: '#DFE1E3',
        'border-color': '#D4D6D7',
        'text-color-internal': '#000000'
      }, {
        color: '#F25A29',
        'border-color': '#DC4717',
        'text-color-internal': '#FFFFFF'
      }, {
        color: '#AD62CE',
        'border-color': '#9453B1',
        'text-color-internal': '#FFFFFF'
      }, {
        color: '#30B6AF',
        'border-color': '#46A39E',
        'text-color-internal': '#FFFFFF'
      }, {
        color: '#FCC940',
        'border-color': '#F3BA25',
        'text-color-internal': '#000000'
      }, {
        color: '#4356C0',
        'border-color': '#3445A2',
        'text-color-internal': '#FFFFFF'
      }, {
        color: '#FF6C7C',
        'border-color': '#EB5D6C',
        'text-color-internal': '#FFFFFF'
      }, {
        color: '#a2cf81',
        'border-color': '#9bbd82',
        'text-color-internal': '#000000'
      }, {
        color: '#f79235',
        'border-color': '#e68f40',
        'text-color-internal': '#000000'
      }, {
        color: '#785cc7',
        'border-color': '#625096',
        'text-color-internal': '#FFFFFF'
      }, {
        color: '#d05e7c',
        'border-color': '#b05b72',
        'text-color-internal': '#FFFFFF'
      }, {
        color: '#3986b7',
        'border-color': '#3a7499',
        'text-color-internal': '#FFFFFF'
        }
    ],
    style: {
      'node': {
        'diameter': '40px',
        'color': '#DFE1E3',
        'border-color': '#D4D6D7',
        'border-width': '2px',
        'text-color-internal': '#000000',
        'caption': '{id}',
        'font-size': '10px'
      },
      'relationship': {
        'color': '#D4D6D7',
        'shaft-width': '1px',
        'font-size': '8px',
        'padding': '3px',
        'text-color-external': '#000000',
        'text-color-internal': '#FFFFFF'
      }
    },
    sizes: [
      {
        diameter: '10px'
      }, {
        diameter: '20px'
      }, {
        diameter: '30px'
      }, {
        diameter: '50px'
      }, {
        diameter: '80px'
      }
    ],
    arrayWidths: [
      {
        'shaft-width': '1px'
      }, {
        'shaft-width': '2px'
      }, {
        'shaft-width': '3px'
      }, {
        'shaft-width': '5px'
      }, {
        'shaft-width': '8px'
      }, {
        'shaft-width': '13px'
      }, {
        'shaft-width': '25px'
      }, {
        'shaft-width': '38px'
      }
    ]
  };
  Selector = (function() {
    function Selector(selector) {
      var _ref;
      _ref = selector.indexOf('.') > 0 ? selector.split('.') : [selector, void 0], this.tag = _ref[0], this.klass = _ref[1];
    }

    Selector.prototype.toString = function() {
      var str;
      str = this.tag;
      if (this.klass != null) {
        str += "." + this.klass;
      }
      return str;
    };

    return Selector;

  })();
  StyleRule = (function() {
    function StyleRule(selector, props) {
      this.selector = selector;
      this.props = props;
    }

    StyleRule.prototype.matches = function(selector) {
      if (this.selector.tag === selector.tag) {
        if (this.selector.klass === selector.klass || !this.selector.klass) {
          return true;
        }
      }
      return false;
    };

    StyleRule.prototype.matchesExact = function(selector) {
      return this.selector.tag === selector.tag && this.selector.klass === selector.klass;
    };

    return StyleRule;

  })();
  StyleElement = (function() {
    function StyleElement(selector, data) {
      this.data = data;
      this.selector = selector;
      this.props = {};
    }

    StyleElement.prototype.applyRules = function(rules) {
      var rule, _i, _j, _len, _len1;
      for (_i = 0, _len = rules.length; _i < _len; _i++) {
        rule = rules[_i];
        if (!(rule.matches(this.selector))) {
          continue;
        }
        neo.utils.extend(this.props, rule.props);
        break;
      }
      for (_j = 0, _len1 = rules.length; _j < _len1; _j++) {
        rule = rules[_j];
        if (!(rule.matchesExact(this.selector))) {
          continue;
        }
        neo.utils.extend(this.props, rule.props);
        break;
      }
      return this;
    };

    StyleElement.prototype.get = function(attr) {
      return this.props[attr] || '';
    };

    return StyleElement;

  })();
  GraphStyle = (function() {
    function GraphStyle(storage) {
      this.storage = storage;
      this.rules = [];
      this.loadRules();
    }

    GraphStyle.prototype.selector = function(item) {
      if (item.isNode) {
        return this.nodeSelector(item);
      } else if (item.isRelationship) {
        return this.relationshipSelector(item);
      }
    };

    GraphStyle.prototype.calculateStyle = function(selector, data) {
      return new StyleElement(selector, data).applyRules(this.rules);
    };

    GraphStyle.prototype.forEntity = function(item) {
      return this.calculateStyle(this.selector(item), item);
    };

    GraphStyle.prototype.forNode = function(node) {
      var selector, _ref;
      if (node == null) {
        node = {};
      }
      selector = this.nodeSelector(node);
      if (((_ref = node.labels) != null ? _ref.length : void 0) > 0) {
        this.setDefaultStyling(selector);
      }
      return this.calculateStyle(selector, node);
    };

    GraphStyle.prototype.forRelationship = function(rel) {
      return this.calculateStyle(this.relationshipSelector(rel), rel);
    };

    GraphStyle.prototype.findAvailableDefaultColor = function() {
      var defaultColor, rule, usedColors, _i, _j, _len, _len1, _ref, _ref1;
      usedColors = {};
      _ref = this.rules;
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        rule = _ref[_i];
        if (rule.props.color != null) {
          usedColors[rule.props.color] = true;
        }
      }
      _ref1 = _style.defaults.colors;
      for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
        defaultColor = _ref1[_j];
        if (usedColors[defaultColor.color] == null) {
          return neo.utils.copy(defaultColor);
        }
      }
      return neo.utils.copy(_style.defaults.colors[0]);
    };

    GraphStyle.prototype.setDefaultStyling = function(selector) {
      var rule;
      rule = this.findRule(selector);
      if (_style.defaults.autoColor && (rule == null)) {
        rule = new StyleRule(selector, this.findAvailableDefaultColor());
        this.rules.push(rule);
        return this.persist();
      }
    };

    GraphStyle.prototype.change = function(item, props) {
      var rule, selector;
      selector = this.selector(item);
      rule = this.findRule(selector);
      if (rule == null) {
        rule = new StyleRule(selector, {});
        this.rules.push(rule);
      }
      neo.utils.extend(rule.props, props);
      this.persist();
      return rule;
    };

    GraphStyle.prototype.destroyRule = function(rule) {
      var idx;
      idx = this.rules.indexOf(rule);
      if (idx != null) {
        this.rules.splice(idx, 1);
      }
      return this.persist();
    };

    GraphStyle.prototype.findRule = function(selector) {
      var r, rule, _i, _len, _ref;
      _ref = this.rules;
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        r = _ref[_i];
        if (r.matchesExact(selector)) {
          rule = r;
        }
      }
      return rule;
    };

    GraphStyle.prototype.nodeSelector = function(node) {
      var selector, _ref;
      if (node == null) {
        node = {};
      }
      selector = 'node';
      if (((_ref = node.labels) != null ? _ref.length : void 0) > 0) {
        selector += "." + node.labels[0];
      }
      return new Selector(selector);
    };

    GraphStyle.prototype.relationshipSelector = function(rel) {
      var selector;
      if (rel == null) {
        rel = {};
      }
      selector = 'relationship';
      if (rel.type != null) {
        selector += "." + rel.type;
      }
      return new Selector(selector);
    };

    GraphStyle.prototype.importGrass = function(string) {
      var e, rules;
      try {
        rules = this.parse(string);
        this.loadRules(rules);
        return this.persist();
      } catch (_error) {
        e = _error;
      }
    };

    GraphStyle.prototype.loadRules = function(data) {
      var props, rule;
      if (!neo.utils.isObject(data)) {
        data = _style.defaults.style;
      }
      this.rules.length = 0;
      for (rule in data) {
        props = data[rule];
        this.rules.push(new StyleRule(new Selector(rule), neo.utils.copy(props)));
      }
      return this;
    };

    GraphStyle.prototype.parse = function(string) {
      var c, chars, insideProps, insideString, k, key, keyword, prop, props, rules, skipThis, v, val, _i, _j, _len, _len1, _ref, _ref1;
      chars = string.split('');
      insideString = false;
      insideProps = false;
      keyword = "";
      props = "";
      rules = {};
      for (_i = 0, _len = chars.length; _i < _len; _i++) {
        c = chars[_i];
        skipThis = true;
        switch (c) {
          case "{":
            if (!insideString) {
              insideProps = true;
            } else {
              skipThis = false;
            }
            break;
          case "}":
            if (!insideString) {
              insideProps = false;
              rules[keyword] = props;
              keyword = "";
              props = "";
            } else {
              skipThis = false;
            }
            break;
          case "'":
          case "\"":
            insideString ^= true;
            break;
          default:
            skipThis = false;
        }
        if (skipThis) {
          continue;
        }
        if (insideProps) {
          props += c;
        } else {
          if (!c.match(/[\s\n]/)) {
            keyword += c;
          }
        }
      }
      for (k in rules) {
        v = rules[k];
        rules[k] = {};
        _ref = v.split(';');
        for (_j = 0, _len1 = _ref.length; _j < _len1; _j++) {
          prop = _ref[_j];
          _ref1 = prop.split(':'), key = _ref1[0], val = _ref1[1];
          if (!(key && val)) {
            continue;
          }
          rules[k][key != null ? key.trim() : void 0] = val != null ? val.trim() : void 0;
        }
      }
      return rules;
    };

    GraphStyle.prototype.persist = function() {
      var _ref;
      return (_ref = this.storage) != null ? _ref.add('grass', JSON.stringify(this.toSheet())) : void 0;
    };

    GraphStyle.prototype.resetToDefault = function() {
      this.loadRules();
      return this.persist();
    };

    GraphStyle.prototype.toSheet = function() {
      var rule, sheet, _i, _len, _ref;
      sheet = {};
      _ref = this.rules;
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        rule = _ref[_i];
        sheet[rule.selector.toString()] = rule.props;
      }
      return sheet;
    };

    GraphStyle.prototype.toString = function() {
      var k, r, str, v, _i, _len, _ref, _ref1;
      str = "";
      _ref = this.rules;
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        r = _ref[_i];
        str += r.selector.toString() + " {\n";
        _ref1 = r.props;
        for (k in _ref1) {
          v = _ref1[k];
          if (k === "caption") {
            v = "'" + v + "'";
          }
          str += "  " + k + ": " + v + ";\n";
        }
        str += "}\n\n";
      }
      return str;
    };

    GraphStyle.prototype.nextDefaultColor = 0;

    GraphStyle.prototype.defaultColors = function() {
      return neo.utils.copy(_style.defaults.colors);
    };

    GraphStyle.prototype.interpolate = function(str, id, properties) {
      return str.replace(/\{([^{}]*)\}/g, function(a, b) {
        var r;
        r = properties[b] || id;
        if (typeof r === 'string' || typeof r === 'number') {
          return r;
        } else {
          return a;
        }
      });
    };

    return GraphStyle;

  })();
  return _style;
})();

var __slice = [].slice;

neo.viz = function(el, graph, layout, style) {
  var clickHandler, force, geometry, onNodeClick, onNodeDblClick, onRelationshipClick, render, viz;
  viz = {
    style: style
  };
  el = d3.select(el);
  geometry = new NeoD3Geometry(style);
  viz.trigger = function() {
    var args, event;
    event = arguments[0], args = 2 <= arguments.length ? __slice.call(arguments, 1) : [];
  };
  onNodeClick = (function(_this) {
    return function(node) {
      return viz.trigger('nodeClicked', node);
    };
  })(this);
  onNodeDblClick = (function(_this) {
    return function(node) {
      return viz.trigger('nodeDblClicked', node);
    };
  })(this);
  onRelationshipClick = (function(_this) {
    return function(relationship) {
      return viz.trigger('relationshipClicked', relationship);
    };
  })(this);
  render = function() {
    var nodeGroups, relationshipGroups, renderer, _i, _j, _len, _len1, _ref, _ref1, _results;
    geometry.onTick(graph);
    nodeGroups = el.selectAll("g.node").attr("transform", function(node) {
      return "translate(" + node.x + "," + node.y + ")";
    });
    _ref = neo.renderers.node;
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      renderer = _ref[_i];
      nodeGroups.call(renderer.onTick, viz);
    }
    relationshipGroups = el.selectAll("g.relationship");
    _ref1 = neo.renderers.relationship;
    _results = [];
    for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
      renderer = _ref1[_j];
      _results.push(relationshipGroups.call(renderer.onTick, viz));
    }
    return _results;
  };
  force = layout.init(render);
  viz.update = function() {
    var height, layers, nodeGroups, nodes, relationshipGroups, relationships, renderer, width, _i, _j, _len, _len1, _ref, _ref1;
    if (!graph) {
      return;
    }
    height = (function() {
      try {
        return parseInt(el.style('height').replace('px', ''));
      } catch (_error) {}
    })();
    width = (function() {
      try {
        return parseInt(el.style('width').replace('px', ''));
      } catch (_error) {}
    })();
    layers = el.selectAll("g.layer").data(["relationships", "nodes"]);
    layers.enter().append("g").attr("class", function(d) {
      return "layer " + d;
    });
    nodes = graph.nodes();
    relationships = graph.relationships();
    relationshipGroups = el.select("g.layer.relationships").selectAll("g.relationship").data(relationships, function(d) {
      return d.id;
    });
    relationshipGroups.enter().append("g").attr("class", "relationship").on("click", onRelationshipClick);
    geometry.onGraphChange(graph);
    _ref = neo.renderers.relationship;
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      renderer = _ref[_i];
      relationshipGroups.call(renderer.onGraphChange, viz);
    }
    relationshipGroups.exit().remove();
    nodeGroups = el.select("g.layer.nodes").selectAll("g.node").data(nodes, function(d) {
      return d.id;
    });
    nodeGroups.enter().append("g").attr("class", "node").call(force.drag).call(clickHandler);
    _ref1 = neo.renderers.node;
    for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
      renderer = _ref1[_j];
      nodeGroups.call(renderer.onGraphChange, viz);
    }
    nodeGroups.exit().remove();
    return force.update(graph, [width, height]);
  };
  clickHandler = neo.utils.clickHandler();
  clickHandler.on('click', onNodeClick);
  clickHandler.on('dblclick', onNodeDblClick);
  return viz;
};

neo.utils.circularLayout = function(nodes, center, radius) {
  var i, n, unlocatedNodes, _i, _len, _results;
  unlocatedNodes = nodes.filter(function(node) {
    return !((node.x != null) && (node.y != null));
  });
  _results = [];
  for (i = _i = 0, _len = unlocatedNodes.length; _i < _len; i = ++_i) {
    n = unlocatedNodes[i];
    n.x = center.x + radius * Math.sin(2 * Math.PI * i / unlocatedNodes.length);
    _results.push(n.y = center.y + radius * Math.cos(2 * Math.PI * i / unlocatedNodes.length));
  }
  return _results;
};

neo.utils.distributeCircular = function(arrowAngles, minSeparation) {
  var angle, center, expand, i, key, length, list, rawAngle, result, run, runLength, runsOfTooDenseArrows, tooDense, wrapAngle, wrapIndex, _i, _j, _k, _len, _ref, _ref1, _ref2, _ref3;
  list = [];
  _ref = arrowAngles.floating;
  for (key in _ref) {
    angle = _ref[key];
    list.push({
      key: key,
      angle: angle
    });
  }
  list.sort(function(a, b) {
    return a.angle - b.angle;
  });
  runsOfTooDenseArrows = [];
  length = function(startIndex, endIndex) {
    if (startIndex < endIndex) {
      return endIndex - startIndex + 1;
    } else {
      return endIndex + list.length - startIndex + 1;
    }
  };
  angle = function(startIndex, endIndex) {
    if (startIndex < endIndex) {
      return list[endIndex].angle - list[startIndex].angle;
    } else {
      return 360 - (list[startIndex].angle - list[endIndex].angle);
    }
  };
  tooDense = function(startIndex, endIndex) {
    return angle(startIndex, endIndex) < length(startIndex, endIndex) * minSeparation;
  };
  wrapIndex = function(index) {
    if (index === -1) {
      return list.length - 1;
    } else if (index >= list.length) {
      return index - list.length;
    } else {
      return index;
    }
  };
  wrapAngle = function(angle) {
    if (angle >= 360) {
      return angle - 360;
    } else {
      return angle;
    }
  };
  expand = function(startIndex, endIndex) {
    if (length(startIndex, endIndex) < list.length) {
      if (tooDense(startIndex, wrapIndex(endIndex + 1))) {
        return expand(startIndex, wrapIndex(endIndex + 1));
      }
      if (tooDense(wrapIndex(startIndex - 1), endIndex)) {
        return expand(wrapIndex(startIndex - 1), endIndex);
      }
    }
    return runsOfTooDenseArrows.push({
      start: startIndex,
      end: endIndex
    });
  };
  for (i = _i = 0, _ref1 = list.length - 2; 0 <= _ref1 ? _i <= _ref1 : _i >= _ref1; i = 0 <= _ref1 ? ++_i : --_i) {
    if (tooDense(i, i + 1)) {
      expand(i, i + 1);
    }
  }
  result = {};
  for (_j = 0, _len = runsOfTooDenseArrows.length; _j < _len; _j++) {
    run = runsOfTooDenseArrows[_j];
    center = list[run.start].angle + angle(run.start, run.end) / 2;
    runLength = length(run.start, run.end);
    for (i = _k = 0, _ref2 = runLength - 1; 0 <= _ref2 ? _k <= _ref2 : _k >= _ref2; i = 0 <= _ref2 ? ++_k : --_k) {
      rawAngle = center + (i - (runLength - 1) / 2) * minSeparation;
      result[list[wrapIndex(run.start + i)].key] = wrapAngle(rawAngle);
    }
  }
  _ref3 = arrowAngles.floating;
  for (key in _ref3) {
    angle = _ref3[key];
    if (!result[key]) {
      result[key] = arrowAngles.floating[key];
    }
  }
  return result;
};

neo.utils.clickHandler = function() {
  var cc, event;
  cc = function(selection) {
    var dist, down, last, tolerance, wait;
    dist = function(a, b) {
      return Math.sqrt(Math.pow(a[0] - b[0], 2), Math.pow(a[1] - b[1], 2));
    };
    down = void 0;
    tolerance = 5;
    last = void 0;
    wait = null;
    selection.on("mousedown", function() {
      d3.event.target.__data__.fixed = true;
      down = d3.mouse(document.body);
      return last = +new Date();
    });
    return selection.on("mouseup", function() {
      if (dist(down, d3.mouse(document.body)) > tolerance) {

      } else {
        if (wait) {
          window.clearTimeout(wait);
          wait = null;
          return event.dblclick(d3.event.target.__data__);
        } else {
          return wait = window.setTimeout((function(e) {
            return function() {
              event.click(e.target.__data__);
              return wait = null;
            };
          })(d3.event), 250);
        }
      }
    });
  };
  event = d3.dispatch("click", "dblclick");
  return d3.rebind(cc, event, "on");
};



neo.utils.measureText = (function() {
  var cache, measureUsingCanvas;
  measureUsingCanvas = function(text, font) {
    var canvas, canvasSelection, context;
    canvasSelection = d3.select('canvas#textMeasurementCanvas').data([this]);
    canvasSelection.enter().append('canvas').attr('id', 'textMeasurementCanvas').style('display', 'none');
    canvas = canvasSelection.node();
    context = canvas.getContext('2d');
    context.font = font;
    return context.measureText(text).width;
  };
  cache = (function() {
    var cacheSize, list, map;
    cacheSize = 10000;
    map = {};
    list = [];
    return function(key, calc) {
      var cached, result;
      cached = map[key];
      if (cached) {
        return cached;
      } else {
        result = calc();
        if (list.length > cacheSize) {
          delete map[list.splice(0, 1)];
          list.push(key);
        }
        return map[key] = result;
      }
    };
  })();
  return function(text, fontFamily, fontSize) {
    var font;
    font = 'normal normal normal ' + fontSize + 'px/normal ' + fontFamily;
    return cache(text + font, function() {
      return measureUsingCanvas(text, font);
    });
  };
})();

(function() {
  var arrowPath, nodeCaption, nodeOutline, nodeOverlay, noop, relationshipOverlay, relationshipType;
  noop = function() {};
  nodeOutline = new neo.Renderer({
    onGraphChange: function(selection, viz) {
      var circles;
      circles = selection.selectAll('circle.outline').data(function(node) {
        return [node];
      });
      circles.enter().append('circle').classed('outline', true).attr({
        cx: 0,
        cy: 0
      });
      circles.attr({
        r: function(node) {
          return node.radius;
        },
        fill: function(node) {
          return viz.style.forNode(node).get('color');
        },
        stroke: function(node) {
          return viz.style.forNode(node).get('border-color');
        },
        'stroke-width': function(node) {
          return viz.style.forNode(node).get('border-width');
        }
      });
      return circles.exit().remove();
    },
    onTick: noop
  });
  nodeCaption = new neo.Renderer({
    onGraphChange: function(selection, viz) {
      var text;
      text = selection.selectAll('text').data(function(node) {
        return node.caption;
      });
      text.enter().append('text').attr({
        'text-anchor': 'middle',
        'font-weight': 'bold',
        'stroke': '#FFFFFF',
        'stroke-width' : '0'
      });
      text.text(function(line) {
        return line.text;
      }).attr('y', function(line) {
        return line.baseline;
      }).attr('font-size', function(line) {
        return viz.style.forNode(line.node).get('font-size');
      }).attr('stroke', function(line) {
        return viz.style.forNode(line.node).get('color');
      }).attr('fill', function(line) {
          return viz.style.forNode(line.node).get('text-color-internal');
      });
      return text.exit().remove();
    },
    onTick: noop
  });
  nodeOverlay = new neo.Renderer({
    onGraphChange: function(selection) {
      var circles;
      circles = selection.selectAll('circle.overlay').data(function(node) {
        if (node.selected) {
          return [node];
        } else {
          return [];
        }
      });
      circles.enter().insert('circle', '.outline').classed('ring', true).classed('overlay', true).attr({
        cx: 0,
        cy: 0,
        fill: '#f5F6F6',
        stroke: 'rgba(151, 151, 151, 0.2)',
        'stroke-width': '3px'
      });
      circles.attr({
        r: function(node) {
          return node.radius + 6;
        }
      });
      return circles.exit().remove();
    },
    onTick: noop
  });
  arrowPath = new neo.Renderer({
    onGraphChange: function(selection, viz) {
      var paths;
      paths = selection.selectAll('path').data(function(rel) {
        return [rel];
      });
      paths.enter().append('path');
      paths.attr('fill', function(rel) {
        return viz.style.forRelationship(rel).get('color');
      }).attr('stroke', 'none');
      return paths.exit().remove();
    },
    onTick: function(selection) {
      return selection.selectAll('path').attr('d', function(d) {
        return d.arrowOutline;
      }).attr('transform', function(d) {
        return isNaN(d.startPoint.x) || isNaN(d.startPoint.y) ? null : "translate(" + d.startPoint.x + " " + d.startPoint.y + ") rotate(" + d.angle + ")";
      });
    }
  });
  relationshipType = new neo.Renderer({
    onGraphChange: function(selection, viz) {
      var texts;
      texts = selection.selectAll("text").data(function(rel) {
        return [rel];
      });
      texts.enter().append("text").attr({
        "text-anchor": "middle"
      });
      texts.attr('font-size', function(rel) {
        return viz.style.forRelationship(rel).get('font-size');
      }).attr('fill', function(rel) {
        return viz.style.forRelationship(rel).get('text-color-' + rel.captionLayout);
      });
      return texts.exit().remove();
    },
    onTick: function(selection, viz) {
      return selection.selectAll('text').attr('x', function(rel) {
        return isNaN(rel.midShaftPoint.x) ? null : rel.midShaftPoint.x;
      }).attr('y', function(rel) {
        return isNaN(rel.midShaftPoint.y) ? null : rel.midShaftPoint.y + parseFloat(viz.style.forRelationship(rel).get('font-size')) / 2 - 1;
      }).attr('transform', function(rel) {
        return isNaN(rel.midShaftPoint.x) || isNaN(rel.midShaftPoint.y) ? null :  "rotate(" + rel.textAngle + " " + rel.midShaftPoint.x + " " + rel.midShaftPoint.y + ")";
      }).text(function(rel) {
        return rel.shortCaption;
      });
    }
  });
  relationshipOverlay = new neo.Renderer({
    onGraphChange: function(selection) {
      var band, rects;
      rects = selection.selectAll("rect").data(function(rel) {
        return [rel];
      });
      band = 20;
      rects.enter().append('rect').classed('overlay', true).attr('fill', 'yellow').attr('x', 0).attr('y', -band / 2).attr('height', band);
      rects.attr('opacity', function(rel) {
        if (rel.selected) {
          return 0.3;
        } else {
          return 0;
        }
      });
      return rects.exit().remove();
    },
    onTick: function(selection) {
      return selection.selectAll('rect').attr('width', function(d) {
        if (d.arrowLength > 0) {
          return d.arrowLength;
        } else {
          return 0;
        }
      }).attr('transform', function(d) {
        return isNaN(d.startPoint.x) || isNaN(d.startPoint.y) ? null : "translate(" + d.startPoint.x + " " + d.startPoint.y + ") rotate(" + d.angle + ")";
      });
    }
  });
  neo.renderers.node.push(nodeOutline);
  neo.renderers.node.push(nodeCaption);
  neo.renderers.node.push(nodeOverlay);
  neo.renderers.relationship.push(arrowPath);
  neo.renderers.relationship.push(relationshipType);
  return neo.renderers.relationship.push(relationshipOverlay);
})();

}());