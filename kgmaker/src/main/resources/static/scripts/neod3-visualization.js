function Neod3Renderer() {

    var styleContents =
        "node {\
          diameter: 40px;\
          color: #DFE1E3;\
          border-color: #D4D6D7;\
          border-width: 2px;\
          text-color-internal: #000000;\
          text-color-external: #000000;\
          caption: '{name}';\
          font-size: 12px;\
        }\
        relationship {\
          color: #4356C0;\
          shaft-width: 3px;\
          font-size: 9px;\
          padding: 3px;\
          text-color-external: #000000;\
          text-color-internal: #FFFFFF;\
        }\n";

    var skip = ["id", "start", "end", "source", "target", "labels", "type", "selected","properties"];
    var prio_props = ["name", "title", "tag", "username", "lastname","caption"];

    var serializer = null;

    var $downloadSvgLink = $('<a href="#" class="btn btn-success visualization-download" target="_blank"><i class="icon-download-alt"></i> Download SVG</a>').hide().click(function () {
        $downloadSvgLink.hide();
    });
    var downloadSvgLink = $downloadSvgLink[0];
    var blobSupport = 'Blob' in window;
    var URLSupport = 'URL' in window && 'createObjectURL' in window.URL;
    var msBlobSupport = typeof window.navigator.msSaveOrOpenBlob !== 'undefined';
    var svgStyling = '<style>\ntext{font-family:sans-serif}\n</style>';
    var stylingUrl = window.location.hostname === 'www.neo4j.org' ? 'http://gist.neo4j.org/css/neod3' : 'styles/neod3';
    if (window.isInternetExplorer) {
        stylingUrl += '-ie.css';
    } else {
        stylingUrl += '.css';
    }

    var existingStyles = {};
    var currentColor = 1;

    function dummyFunc() {
    }

    function render(id, $container, visualization) {
        function extract_props(pc) {
            var p = {};
            for (var key in pc) {
                if (!pc.hasOwnProperty(key) || skip.indexOf(key) != -1) continue;
                p[key] = pc[key];
            }
            return p;
        }

        function node_styles(nodes) {
            function label(n) {
                var labels = n["labels"];
                if (labels && labels.length) {
                    return labels[labels.length - 1];
                }
                return "";
            }

            var style = {};
            for (var i = 0; i < nodes.length; i++) {
                var props= nodes[i].properties = extract_props(nodes[i]);
                var keys = Object.keys(props);
                if (label(nodes[i]) !== "" && keys.length > 0) {
                    var selected_keys = prio_props.filter(function (k) {
                        return keys.indexOf(k) !== -1
                    });
                    selected_keys = selected_keys.concat(keys).concat(['id']);
                    var selector = "node." + label(nodes[i]);
                    var selectedKey = selected_keys[0];
                    if (typeof(props[selectedKey]) === "string" && props[selectedKey].length > 30) {
                        props[selectedKey] = props[selectedKey].substring(0,30)+" ...";
                    }
                    style[selector] = style[selector] || selectedKey;
                }
            }
            return style;
        }
        function style_sheet(styles, styleContents) {
            function format(key) {
                var item=styles[key];
                return item.selector +
                    " {caption: '{" + item.caption +
                    "}'; color: " + item.color +
                    "; border-color: " + item['border-color'] +
                    "; text-color-internal: " +  item['text-color-internal'] +
                    "; text-color-external: " +  item['text-color-external'] +
                    "; }"
            }
            return styleContents + Object.keys(styles).map(format).join("\n");
        }
        function create_styles(styleCaptions,  styles) {
            var colors = neo.style.defaults.colors;
            for (var selector in styleCaptions) {
                if (!(selector in styles)) {
                    var color = colors[currentColor];
                    currentColor = (currentColor + 1) % colors.length;
                    var textColor = window.isInternetExplorer ? '#000000' : color['text-color-internal'];
                    var style = {selector:selector, caption:styleCaptions[selector], color:color.color, 
                         "border-color":color['border-color'], "text-color-internal":textColor,"text-color-external": textColor }
                    styles[selector] = style;
                }
            }
            return styles;
        }

        function applyZoom() {
            renderer.select(".nodes").attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
            renderer.select(".relationships").attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
        }

        function enableZoomHandlers() {
            renderer.on("wheel.zoom",zoomHandlers.wheel);
            renderer.on("mousewheel.zoom",zoomHandlers.mousewheel);
            renderer.on("mousedown.zoom",zoomHandlers.mousedown);
            renderer.on("DOMMouseScroll.zoom",zoomHandlers.DOMMouseScroll);
            renderer.on("touchstart.zoom",zoomHandlers.touchstart);
            renderer.on("touchmove.zoom",zoomHandlers.touchmove);
            renderer.on("touchend.zoom",zoomHandlers.touchend);
        }

        function disableZoomHandlers() {
            renderer.on("wheel.zoom",null);
            renderer.on("mousewheel.zoom",null);
            renderer.on("mousedown.zoom", null);
            renderer.on("DOMMouseScroll.zoom", null);
            renderer.on("touchstart.zoom",null);
            renderer.on("touchmove.zoom",null);
            renderer.on("touchend.zoom",null);
        }

        function legend(svg, styles) {
          var keys = Object.keys(styles).sort();
          var circles = svg.selectAll('circle.legend').data(keys);
          var r=20;
          circles.enter().append('circle').classed('legend', true).attr({
            cx: 2*r,
            r : r
          });
          circles.attr({
            cy: function(node) {
              return (keys.indexOf(node)+1)*2.2*r;
            },
            fill: function(node) {
              return styles[node]['color'];
            },
            stroke: function(node) {
              return styles[node]['border-color'];
            },
            'stroke-width': function(node) {
              return "2px";
            }
          });
          var text = svg.selectAll('text.legend').data(keys);
          text.enter().append('text').classed('legend',true).attr({
            'text-anchor': 'left',
            'font-weight': 'bold',
            'stroke-width' : '0',
            'stroke-color' : 'black',
            'fill' : 'black',
            'x' : 3.2*r,
            'font-size' : "12px"
          });
          text.text(function(node) {
            var label = styles[node].selector;
            return label ? label.substring(5) : "";
          }).attr('y', function(node) {
              return (keys.indexOf(node)+1)*2.2*r+6;
          })
/*
          .attr('stroke', function(node) {
            return styles[node]['color'];
          })
         .attr('fill', function(node) {
              return styles[node]['text-color-internal'];
          });
*/
          return circles.exit().remove();
        }
        function keyHandler() {
            if (d3.event.altKey || d3.event.shiftKey) {
                enableZoomHandlers();
            }
            else {
               disableZoomHandlers();
            }
        }

        var links = visualization.links;
        var nodes = visualization.nodes;
        for (var i = 0; i < links.length; i++) {
            links[i].source = links[i].start;
            links[i].target = links[i].end;
           //  links[i].properties = props(links[i]);
        }
        var nodeStyles = node_styles(nodes);
        create_styles(nodeStyles, existingStyles);
        var styleSheet = style_sheet(existingStyles, styleContents);
        var graphModel = neo.graphModel()
            .nodes(nodes)
            .relationships(links);
        var graphView = neo.graphView()
            .style(styleSheet)
            .width($container.width()).height($container.height()).on('nodeClicked', dummyFunc).on('relationshipClicked', dummyFunc).on('nodeDblClicked', dummyFunc);
        var svg = d3.select("#" + id).append("svg");
        var renderer = svg.data([graphModel]);
        legend(svg,existingStyles);
        var zoomHandlers = {};
        var zoomBehavior = d3.behavior.zoom().on("zoom", applyZoom).scaleExtent([0.2, 8]);

        renderer.call(graphView);
        renderer.call(zoomBehavior);

        zoomHandlers.wheel = renderer.on("wheel.zoom");
        zoomHandlers.mousewheel = renderer.on("mousewheel.zoom");
        zoomHandlers.mousedown = renderer.on("mousedown.zoom");
        zoomHandlers.DOMMouseScroll = renderer.on("DOMMouseScroll.zoom");
        zoomHandlers.touchstart = renderer.on("touchstart.zoom");
        zoomHandlers.touchmove = renderer.on("touchmove.zoom")
        zoomHandlers.touchend = renderer.on("touchend.zoom");
        disableZoomHandlers();

        d3.select('body').on("keydown", keyHandler).on("keyup", keyHandler);

        function refresh() {
            graphView.height($container.height());
            graphView.width($container.width());
            renderer.call(graphView);
        }

        function saveToSvg() {
            var svgElement = $('#' + id).children('svg').first()[0];
            var xml = serializeSvg(svgElement, $container);
            if (!msBlobSupport && downloadSvgLink.href !== '#') {
                window.URL.revokeObjectURL(downloadSvgLink.href);
            }
            var blob = new window.Blob([xml], {
                'type': 'image/svg+xml'
            });
            var fileName = id + '.svg';
            if (!msBlobSupport) {
                downloadSvgLink.href = window.URL.createObjectURL(blob);
                $downloadSvgLink.appendTo($container).show();
                $downloadSvgLink.attr('download', fileName);
            } else {
                window.navigator.msSaveOrOpenBlob(blob, fileName);
            }
        }

        function getFunctions() {
            var funcs = {};
            if (blobSupport && (URLSupport || msBlobSupport)) {
                funcs['icon-download-alt'] = {'title': 'Save as SVG', 'func':saveToSvg};
            }
            return funcs;
        }

        return  {
            'subscriptions': {
                'expand': refresh,
                'contract': refresh,
                'sizeChange': refresh
            },
            'actions': getFunctions()
        };
    }

    function serializeSvg(element, $container) {
        if (serializer === null) {
            if (typeof window.XMLSerializer !== 'undefined') {
                var xmlSerializer = new XMLSerializer();
                serializer = function (emnt) {
                    return xmlSerializer.serializeToString(emnt);
                };
            } else {
                serializer = function (emnt) {
                    return '<svg xmlns="http://www.w3.org/2000/svg">' + $(emnt).html() + '</svg>';
                }
            }
        }
        var svg = serializer(element);
        svg = svg.replace('<svg ', '<svg height="' + $container.height() + '" width="' + $container.width() + '" ')
            .replace(/<g/, '\n' + svgStyling + '\n<g');
        return svg;
    }

    $.get(stylingUrl, function (data) {
        svgStyling = '<style>\n' + data + '\n</style>';
        $(svgStyling).appendTo('head');
    });

    return {'render': render};
}
