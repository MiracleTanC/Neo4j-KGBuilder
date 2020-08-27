<template>
  <div>
    <div id="gid_tc" style="float:left;">
      <div id="gid"></div>
      <ul
        class="el-dropdown-menu el-popper"
        id="my_custom_menu"
        style="display: none;"
      >
        <li class="el-dropdown-menu__item" @click="btnOpenNode">
          <svg class="ctwh_icon" aria-hidden="true">
            <use xlink:href="#icon-zk-all"></use>
          </svg>
          <span class="pl-15">展开</span>
        </li>
        <li class="el-dropdown-menu__item" @click="btnCollapseNode">
          <svg class="ctwh_icon" aria-hidden="true">
            <use xlink:href="#icon-shouqi"></use>
          </svg>
          <span class="pl-15">收起</span>
        </li>
      </ul>
      <div
        class="el-dropdown-menu el-popper"
        id="richContainer"
        style="display: none;width: 420px;height: 300px;position: absolute; left: 222px; top: 75px;"
      >
        <span
          @click="close"
          class="close-x"
          style="
                float: right;
                padding: 4px 8px;
                cursor: pointer;
                color: rgb(236, 105, 65);font-size: 16px;margin: -20px -33px 0 0;background: #fff;border-radius: 50%;border: 1px solid #ddd;z-index: 999;font-family: '微软雅黑';"
          >X</span
        >
        <div class="search_result_list_min">
          <div
            :key="'m_' + index"
            v-for="(item, index) in nodeRecordList"
            class="search_result_item_min"
          >
            <div>
              <div class="datatitle">
                <a href="javascript:;" @click="linkto(item)"
                  >{{ index + 1 }}.{{ item.条目名称 }}
                </a>
              </div>
              <div class="datasource">
                <span>{{ item.工具书名称 }}</span>
              </div>
            </div>
            <span class="datacontent" @click="linkto(item)" v-html="item.快照">
              {{ item.快照 }}
            </span>
          </div>
        </div>
      </div>
      <div class="mengceng"></div>
    </div>
    <div class="svg-set-box clearfix">
      <div class="ctwh-dibmr">
        <span>显示范围：</span>
        <a
          :key="index"
          v-for="(m, index) in pagesizelist"
          @click="setmatchsize(m, this)"
          href="javascript:void(0)"
          :class="[m.isactive ? 'sd-active' : '', 'ss-d sd' + (index + 1)]"
        >
        </a>
      </div>
      <div class="ctwh-dibmr" style="float: right">
        <ul class="toolbar">
          <li>
            <a href="javascript:;" @click="zoomin"
              ><span><i class="el-icon-zoom-in"></i>放大</span></a
            >
          </li>
          <li>
            <a href="javascript:;" @click="zoomout"
              ><span><i class="el-icon-zoom-out"></i>缩小</span></a
            >
          </li>
          <li>
            <a href="javascript:;" @click="refresh"
              ><span><i class="el-icon-refresh-right"></i>还原</span></a
            >
          </li>
          <li>
            <a
              v-if="!isFullscreen"
              id="fullscreenbtn"
              href="javascript:;"
              @click="showFull"
            >
              <span><i class="el-icon-full-screen"></i>全屏</span>
            </a>
            <a
              v-else
              id="fullscreenbtn"
              href="javascript:;"
              @click="exitfullscreen"
            >
              <span><i class="el-icon-full-screen"></i>退出全屏</span>
            </a>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>
<script>
import axios from "axios";
import * as d3 from "d3";
import $ from "jquery";

export default {
  props: ["pid"],
  data() {
    return {
      qaGraphNode: {},
      qaGraphNodeText: {},
      qaGraphLink: {},
      qaGraphLinkText: {},
      theme: 0,
      loading: false,
      width: 1000,
      height: 800,
      gcontainer: {},
      svg: {},
      zoom: null,
      arrowMarker: {},
      simulation: {},
      isFullscreen: false,
      graph: {
        nodes: [],
        links: []
      },
      colorList: [
        "#ff8373",
        "#f9c62c",
        "#a5ca34",
        "#6fce7a",
        "#70d3bd",
        "#ea91b0"
      ],
      pagesizelist: [
        { size: 100, isactive: false },
        { size: 300, isactive: false },
        { size: 500, isactive: true },
        { size: 1000, isactive: false }
      ],
      nodeRecordList: []
    };
  },
  components: {},
  mounted() {
    this.initGraphContainer();
    this.addmaker();
    this.initGraph();
  },
  created() {},
  watch: {},
  methods: {
    initGraphContainer() {
      this.gcontainer = d3.select("#gid");
      if (this.isFullscreen) {
        this.width = window.screen.width;
        this.height = window.screen.height;
      } else {
        this.width = $("#" + this.pid).width();
        this.height = $("#" + this.pid).height();
      }
      this.svg = this.gcontainer.append("svg");
      var sWidth = this.width;
      var sHeight = this.height;
      this.svg.attr("width", sWidth);
      this.svg.attr("height", sHeight);
      // this.svg.attr("viewBox", "0 0 " + sWidth / 2 + " " + sHeight / 2);
      this.svg.attr("id", "svg_idx");
      this.svg.attr("preserveAspectRatio", "xMidYMidmeet");
      this.simulation = d3
        .forceSimulation()
        .force("charge", d3.forceManyBody().strength(-1500))
        .force(
          "link",
          d3
            .forceLink()
            .distance(60)
            .id(function(d) {
              return d.uuid;
            })
        )
        .force("collide", d3.forceCollide().strength(-30))
        .force("center", d3.forceCenter(this.width / 2, this.height / 2));
      this.qaGraphLink = this.svg.append("g").attr("class", "line");
      this.qaGraphLinkText = this.svg.append("g").attr("class", "linetext");
      this.qaGraphNode = this.svg.append("g").attr("class", "node");
      this.qaGraphNodeText = this.svg.append("g").attr("class", "nodetext");
    },
    initGraph() {
      var _this = this;
      axios.get("/static/kgData.json", {}).then(function(response) {
        var data = response.data;
        _this.graph.nodes = data.node;
        _this.graph.links = data.relationship;
        _this.updategraph();
      });
    },
    addmaker() {
      var arrowMarker = this.svg
        .append("marker")
        .attr("id", "arrow")
        .attr("markerUnits", "strokeWidth")
        .attr("markerWidth", "12") //
        .attr("markerHeight", "12")
        .attr("viewBox", "0 0 12 12")
        .attr("refX", "30")
        .attr("refY", "6")
        .attr("orient", "auto");
      var arrowPath = "M2,2 L10,6 L2,10 L6,6 L2,2"; // 定义箭头形状
      arrowMarker
        .append("path")
        .attr("d", arrowPath)
        .attr("fill", "#ccc");
    },
    drawnode(node) {
      var _this = this;
      var nodeEnter = node.enter().append("circle");
      nodeEnter.on("click", function(d) {
        console.log("触发单击:" + d);

        // eslint-disable-next-line no-debugger
        debugger;
        _this.opennode();
        console.log("ddd");
        //
      });
      nodeEnter.on("dblclick", function(d) {
        event.preventDefault();
        console.log("触发双击:" + d);
      });
      nodeEnter.call(
        d3
          .drag()
          .on("start", _this.dragstarted)
          .on("drag", _this.dragged)
          .on("end", _this.dragended)
      );
      return nodeEnter;
    },
    opennode() {
      var _this = this;
      var noddd = [
        {
          flag: "1",
          code: "27301",
          parentCode: "273",
          grade: "2",
          name: "儒家2",
          uuid: "4617858011"
        },
        {
          code: "2730107",
          flag: "1",
          parentCode: "27301",
          grade: "3",
          name: "故事轶闻2",
          uuid: "2636501111"
        }
      ];
      var newships = [
        {
          sourceid: "46178580",
          targetid: "2636501111",
          name: "",
          targetcode: "2730107",
          uuid: "91804213",
          sourcecode: "27301"
        },
        {
          sourceid: "46178580",
          targetid: "4617858011",
          name: "",
          targetcode: "273010723",
          uuid: "91804389",
          sourcecode: "2730107"
        }
      ];
      _this.graph.nodes = _this.graph.nodes.concat(noddd);
      _this.graph.links = _this.graph.links.concat(newships);
      _this.updategraph();
    },
    drawnodetext(nodetext) {
      var _this = this;
      var nodetextEnter = nodetext.enter().append("text");
      nodetextEnter.call(
        d3
          .drag()
          .on("start", _this.dragstarted)
          .on("drag", _this.dragged)
          .on("end", _this.dragended)
      );
      return nodetextEnter;
    },
    drawlink(link) {
      var _this = this;
      var linkEnter = link
        .enter()
        .append("line")
        .attr("stroke-width", 1)
        .attr("stroke", function() {
          return _this.colorList[2];
        })
        .attr("marker-end", "url(#arrow)"); // 箭头
      return linkEnter;
    },
    drawlinktext(linktext) {
      var linktextEnter = linktext
        .enter()
        .append("text")
        .attr("class", "linetext")
        .style("fill", "#875034")
        .style("font-size", "16px")
        .text(function(d) {
          return d.lk.name;
        });
      return linktextEnter;
    },
    updategraph() {
      var _this = this;
      var lks = _this.graph.links;
      var nodes = _this.graph.nodes;
      nodes.forEach(function(n) {
        if (n.center === 1 || n.center === "1") {
          n.fx = _this.width / 2;
          n.fy = _this.height / 2;
        }
        if (typeof n.fx === "undefined" || n.fx === "") {
          n.fx = null;
        }
        if (typeof n.fy === "undefined" || n.fy === "") {
          n.fy = null;
        }
      });
      var links = [];
      // eslint-disable-next-line no-debugger
      debugger;
      lks.forEach(function(m) {
        var sourceNode = nodes.filter(function(n) {
          return n.uuid === m.sourceid;
        })[0];
        if (typeof sourceNode === "undefined") return;
        var targetNode = nodes.filter(function(n) {
          return n.uuid === m.targetid;
        })[0];
        if (typeof targetNode === "undefined") return;
        links.push({ source: sourceNode.uuid, target: targetNode.uuid, lk: m });
      });
      // 更新节点
      //_this.qaGraphNode = _this.drawnode(nodes);
      var node = _this.qaGraphNode.selectAll("circle").data(nodes, function(d) {
        return d.uuid;
      });
      node.exit().remove();
      var nodeEnter = _this.drawnode(node);
      node = nodeEnter.merge(node).text(function(d) {
        return d.name;
      });
      node.attr("r", 25);
      node.attr("fill", "red");
      node
        .append("title") // 为每个节点设置title
        .text(function(d) {
          return d.name;
        });
      // 更新节点文字
      //_this.qaGraphNodeText = _this.drawnodetext(nodes);
      var nodetext = _this.qaGraphNodeText
        .selectAll("text")
        .data(nodes, function(d) {
          return d.uuid;
        });
      nodetext.exit().remove();
      var nodetextEnter = _this.drawnodetext(nodetext);
      nodetext = nodetextEnter.merge(nodetext).text(function(d) {
        return d.name;
      });
      nodetext
        .style("fill", function() {
          if (_this.theme === 0) {
            return "#333";
          } else {
            return "#fff";
          }
        })
        .attr("class", "nodetext")
        .attr("dy", "3.6em")
        .attr("font-family", "宋体")
        .attr("font-size", 16)
        .attr("text-anchor", "middle")
        .text(function(d) {
          return d.name;
        });
      nodetext
        .append("title") // 为每个节点设置title
        .text(function(d) {
          if (typeof d.name !== "undefined") {
            return d.name;
          }
          return "";
        });
      // 更新连线 links
      // _this.qaGraphLink = _this.drawlink(links);
      var link = _this.qaGraphLink.selectAll("line").data(links, function(d) {
        return d.uuid;
      });
      link.exit().remove();
      var linkEnter = _this.drawlink(link);
      link = linkEnter.merge(link);
      // 更新连线文字
      //_this.qaGraphLinkText = _this.drawlinktext(links);
      var linktext = _this.qaGraphLinkText
        .selectAll("text")
        .data(links, function(d) {
          return d.uuid;
        });
      linktext.exit().remove();
      var linktextEnter = _this.drawlinktext(linktext);
      linktext = linktextEnter.merge(linktext).text(function(d) {
        return d.lk.name;
      });
      _this.simulation
        .nodes(nodes)
        .alphaTarget(0)
        .alphaDecay(0.05)
        .on("tick", ticked);
      function ticked() {
        // 更新连线坐标
        link
          .attr("x1", function(d) {
            return d.source.x;
          })
          .attr("y1", function(d) {
            return d.source.y;
          })
          .attr("x2", function(d) {
            return d.target.x;
          })
          .attr("y2", function(d) {
            return d.target.y;
          });
        // 刷新连接线上的文字位置
        linktext
          .attr("x", function(d) {
            if (
              typeof d.source.x === "undefined" ||
              typeof d.target.x === "undefined"
            )
              return 0;
            var x = (parseFloat(d.source.x) + parseFloat(d.target.x)) / 2;
            return x;
          })
          .attr("y", function(d) {
            if (
              typeof d.source.y === "undefined" ||
              typeof d.target.y === "undefined"
            )
              return 0;
            var y = (parseFloat(d.source.y) + parseFloat(d.target.y)) / 2;
            return y;
          });
        // 更新节点坐标
        node
          .attr("cx", function(d) {
            return d.x;
          })
          .attr("cy", function(d) {
            return d.y;
          });
        // 更新文字坐标
        nodetext
          .attr("x", function(d) {
            return d.x;
          })
          .attr("y", function(d) {
            return d.y;
          });
      }

      _this.simulation.force("link").links(links);
      _this.simulation.force(
        "center",
        d3.forceCenter(_this.width / 2, _this.height / 2)
      );
      _this.simulation.alpha(1).restart();
      // 鼠标滚轮缩放
      _this.zoom = d3
        .zoom()
        .scaleExtent([0.1, 4])
        .on("zoom", _this.zoomed);
      _this.svg.call(_this.zoom);
      _this.svg.on("dblclick.zoom", null); // 静止双击缩放
    },
    dragstarted(d) {
      if (!d3.event.active) this.simulation.alphaTarget(0.3).restart();
      d.fx = d.x;
      d.fy = d.y;
    },
    dragged(d) {
      d.fx = d3.event.x;
      d.fy = d3.event.y;
    },
    dragended(d) {
      if (!d3.event.active) this.simulation.alphaTarget(0);
      d.fx = d3.event.x;
      d.fy = d3.event.y;
    },
    zoomed() {
      this.svg.selectAll("g").attr("transform", d3.event.transform);
    },
    zoomClick(direction) {
      var self = this;
      var factor = 0.2;
      var targetZoom = 1;
      var extent = self.zoom.scaleExtent();
      targetZoom = 1 + factor * direction;
      if (targetZoom < extent[0] || targetZoom > extent[1]) {
        return false;
      }
      self.zoom.scaleBy(self.svg, targetZoom); // 执行该方法后 会触发zoom事件
    },
    zoomin() {
      this.zoomClick(1);
    },
    zoomout() {
      this.zoomClick(-1);
    },
    refresh() {
      this.svg.call(this.zoom.transform, d3.zoomIdentity);
    },
    showFull() {
      this.isFullscreen = !this.isFullscreen;
      var full = document.getElementById("kg_container");
      this.fullscreen(full);
    },
    fullscreen(element) {
      if (element.requestFullscreen) {
        element.requestFullscreen();
      } else if (element.mozRequestFullScreen) {
        element.mozRequestFullScreen();
      } else if (element.webkitRequestFullscreen) {
        element.webkitRequestFullscreen();
      } else if (element.msRequestFullscreen) {
        element.msRequestFullscreen();
      }
    },
    exitfullscreen() {
      this.isFullscreen = !this.isFullscreen;
      if (document.exitFullscreen) {
        document.exitFullscreen();
      } else if (document.mozCancelFullScreen) {
        document.mozCancelFullScreen();
      } else if (document.webkitExitFullscreen) {
        document.webkitExitFullscreen();
      }
    },
    btnCollapseNode() {},
    btnOpenNode() {},
    close() {}
  }
};
</script>
<style>
.svg-set-box {
  height: 46px;
  line-height: 46px;
  padding-left: 15px;
  color: #7f7f7f;
  /* background: #f7f7f7; */
  position: absolute;
  bottom: 0;
}
.ctwh-dibmr {
  display: inline-block;
}
.ss-d {
  display: inline-block;
  vertical-align: middle;
  margin-right: 10px;
  border-radius: 50%;
  background: #dedede;
}
.sd1 {
  width: 30px;
  height: 30px;
}
.sd2 {
  width: 26px;
  height: 26px;
}
.sd3 {
  width: 20px;
  height: 20px;
}
.sd4 {
  width: 16px;
  height: 16px;
}
.sd-active {
  background: #08aefc !important;
}
.toolbar {
  margin-left: 150px;
  margin-right: 15px;
  line-height: 18px;
}
ul,
li {
  list-style: none;
}
.toolbar li {
  float: left;
  width: 60px;
}
</style>
