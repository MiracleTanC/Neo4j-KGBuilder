<template>
  <div
    v-loading="fullscreenLoading"
    id="BOX-SVG"
    class="BOX-SVG"
    :style="styles"
  >
    <div
      id="SVG"
      class="SVG"
      @click="initContainerLeftClick"
      @contextmenu.prevent="initContainerRightClick"
    />
    <menuLink ref="menu_link" />
    <menuBlank ref="menu_blank" @changeCursor="changeCursor" />
  </div>
</template>
<script>
import * as d3 from "d3";
import _ from "lodash";
import { EventBus } from "@/utils/event-bus.js";
import menuLink from "@/components/KGBuilderMenuLink";
import menuBlank from "@/components/KGBuilderMenuBlank";
import SvgIcon from "@/components/SvgIcon.vue";
export default {
  name: "KGBuilder2",
  inject: [
    "_thisKey",
    "Dset",
    "createSingleNode",
    "updateCoordinateOfNode"
  ],
  components: {
    menuLink,
    menuBlank,
    SvgIcon
  },
  props: {
    styles: {
      type: Object,
      default: () => {}
    },
    initData: {
      type: Object,
      default: {}
    },
    ringFunction: {
      type: Array,
      default: []
    },
    domainId: {
      type: Object,
      default: 0
    },
    domainId: {
      type: Number,
      default: ""
    }
  },
  data() {
    return {
      fullscreenLoading: false,
      style: {},
      // 缩放配置
      zoom: d3
        .zoom()
        .scaleExtent([-10, 10])
        .on("zoom", function() {
          d3.select("#link_menubar").style("display", "none");
          d3.select("#nodeDetail").style("display", "none");
          d3.selectAll(".node").attr("transform", d3.event.transform);
          d3.selectAll(".nodeText").attr("transform", d3.event.transform);
          d3.selectAll(".line").attr("transform", d3.event.transform);
          d3.selectAll(".lineText").attr("transform", d3.event.transform);
          d3.selectAll(".nodeSymbol").attr("transform", d3.event.transform);
          d3.selectAll(".nodeButton").attr("transform", d3.event.transform);
        }),
      updateLink: null,
      editLink: null, // 编辑连线
      editLinkState: false,
      clone: null,
      scale: null,
      selectNode: {
        uuid: "",
        cname: "",
        fx: "",
        fy: ""
      },
      // d3属性
      svg: null,
      simulation: null,
      linkGroup: null,
      linkTextGroup: null,
      nodeGroup: null,
      nodeTextGroup: null,
      nodeSymbolGroup: null,
      nodeButtonGroup: null,
      nodeButtonAction: "",
      graph: {
        nodes: [],
        links: []
      },
      widht: null,
      height: null,
      isAddLink: false
    };
  },
  watch: {
    initData: {
      handler(newvalue) {
        this.fullscreenLoading = true;
        //console.log(newvalue)
        const data = JSON.parse(JSON.stringify(newvalue));
        this.scale = 1;
        this.graph.nodes = data.nodes;
        this.graph.links = data.links;
        if (this.svg) {
          this.updateGraph();
        }
        this.fullscreenLoading = false;
      },
      deep: true,
      immediate: true
    }
  },
  mounted() {
    const _this = this;
    _this.$nextTick(() => {
      _this._thisKey(this);
      _this.Dset(d3);
    });
    EventBus.$on("DIV", (d, x) => {
      this.width = d;
      this.height = x;
      _this.initGraph();
    });
  },
  methods: {
    //画布右击
    initContainerRightClick(event) {
      let _this = this;
      let menuBar = {
        left: event.clientX - 300,
        top: event.clientY - 140,
        show: true
      };
      _this.$refs.menu_blank.init(menuBar);
      event.preventDefault();
    },
    //画布点击
    initContainerLeftClick(event) {
      let _this = this;
      _this.$refs.menu_blank.init({ show: false });
      _this.$refs.menu_link.init({ show: false });
      //_this.$refs.node_richer.close();
      if (event.target.tagName != "circle" && event.target.tagName != "link") {
        d3.select("#nodeDetail").style("display", "none");
      }
      let cursor = document.getElementById("BOX-SVG").style.cursor;
      if (cursor == "crosshair") {
        d3.select(".BOX-SVG").style("cursor", "default");
        _this.createSingleNode(event.offsetX, event.offsetY);
      }
      event.preventDefault();
    },
    // 初始化画布配置
    initGraph() {
      const graphContainer = d3.select(".SVG");
      this.svg = graphContainer.append("svg");
      this.svg.attr("width", this.width);
      this.svg.attr("height", this.height);
      this.svg.attr("preserveAspectRatio", "xMidYMidmeet");
      this.simulation = d3
        .forceSimulation()
        .force(
          "link",
          d3
            .forceLink()
            .distance(function(d) {
              return 60;
              // return Math.floor(Math.random() * (700 - 200)) ;
            })
            .id(function(d) {
              return d.uuid;
            })
        )
        .force("charge", d3.forceManyBody().strength(-400))
        .force("collide", d3.forceCollide())
        .force("center", d3.forceCenter(this.width / 2, this.height / 2));
      this.linkGroup = this.svg.append("g").attr("class", "line");
      this.linkTextGroup = this.svg.append("g").attr("class", "lineText");
      this.nodeGroup = this.svg.append("g").attr("class", "node");
      this.nodeTextGroup = this.svg.append("g").attr("class", "nodeText");
      this.nodeSymbolGroup = this.svg.append("g").attr("class", "nodeSymbol");
      this.nodeButtonGroup = this.svg.append("g").attr("class", "nodeButton");
      this.addMaker();
      this.svg.on(
        "click",
        function() {
          //console.log('svg click')
          d3.selectAll(".buttongroup").classed("circle_none", true);
          d3.selectAll("g[id^='circle_menu_']").style("display", "none");
        },
        false
      );
      this.simulation.alphaTarget(0.1).restart();
    },
    // 更新画布数据
    updateGraph() {
      const _this = this;
      const lks = this.graph.links;
      const nodes = this.graph.nodes;
      const links = [];
      nodes.forEach(function(n) {
        if (n.center === 1 || n.center === "1") {
          n.fx = _this.width / 2;
          n.fy = _this.height / 2;
        }
        if (typeof n.fx === "undefined" || n.fx === "") {
          n.fx = null;
        } else {
          n.fx = parseFloat(n.fx);
        }
        if (typeof n.fy === "undefined" || n.fy === "") {
          n.fy = null;
        } else {
          n.fy = parseFloat(n.fy);
        }
        if (typeof n.r === "undefined" || n.r === "") {
          n.r = 25;
        } else {
          n.r = parseFloat(n.r);
        }
      });
      lks.forEach(function(m) {
        const sourceNode = nodes.filter(function(n) {
          return n.uuid === m.sourceId;
        })[0];
        if (typeof sourceNode === "undefined") return;
        const targetNode = nodes.filter(function(n) {
          return n.uuid === m.targetId;
        })[0];
        if (typeof targetNode === "undefined") return;
        links.push({ source: sourceNode.uuid, target: targetNode.uuid, lk: m });
      });
      // 为每一个节点定制按钮组
      this.addNodeButton();
      // 连线多个弯曲
      if (links.length > 0) {
        _.each(links, function(link) {
          const same = _.filter(links, {
            source: link.source,
            target: link.target
          });
          const sameAlt = _.filter(links, {
            source: link.target,
            target: link.source
          });
          const sameAll = same.concat(sameAlt);
          _.each(sameAll, function(s, i) {
            s.sameIndex = i + 1;
            s.sameTotal = sameAll.length;
            s.sameTotalHalf = s.sameTotal / 2;
            s.sameUneven = s.sameTotal % 2 !== 0;
            s.sameMiddleLink =
              s.sameUneven === true &&
              Math.ceil(s.sameTotalHalf) === s.sameIndex;
            s.sameLowerHalf = s.sameIndex <= s.sameTotalHalf;
            s.sameArcDirection = s.sameLowerHalf ? 0 : 1;
            s.sameIndexCorrected = s.sameLowerHalf
              ? s.sameIndex
              : s.sameIndex - Math.ceil(s.sameTotalHalf);
          });
        });
        const maxSame = _.chain(links)
          .sortBy(function(x) {
            return x.sameTotal;
          })
          .last()
          .value().sameTotal;

        _.each(links, function(link) {
          link.maxSameHalf = Math.round(maxSame / 2);
        });
      }
      // 更新连线 links
      d3.selectAll(".line >path").remove();
      let link = this.linkGroup.selectAll(".line >path").data(links);
      link.exit().remove();
      const linkEnter = this.drawLink(link);
      link = linkEnter.merge(link);
      // // 更新连线文字
      d3.selectAll(".lineText >g").remove();
      // 更新连线文字
      const linktext = this.linkTextGroup.selectAll("g").data(links);
      linktext.exit().remove();
      this.drawLinkText(linktext);
      // 更新节点按钮组
      d3.selectAll(".nodeButton >g").remove();
      let nodeButton = this.nodeButtonGroup
        .selectAll(".nodeButton")
        .data(nodes, function(d) {
          return d;
        });
      nodeButton.exit().remove();
      const nodeButtonEnter = this.drawNodeButton(nodeButton);
      nodeButton = nodeButtonEnter.merge(nodeButton);
      // 更新节点
      this.nodeGroup.selectAll(".node >g").remove();
      let node = this.nodeGroup.selectAll(".node >g").data(nodes);
      node.exit().remove();
      const nodeEnter = this.drawNode(node);
      node = nodeEnter.merge(node);
      // 更新节点文字
      this.nodeTextGroup.selectAll(".nodeText >g").remove();
      let nodeText = this.nodeTextGroup.selectAll(".nodeText >g").data(nodes);
      nodeText.exit().remove();
      const nodeTextEnter = this.drawNodeText(nodeText);
      nodeText = nodeTextEnter.merge(nodeText);
      // 更新节点标识
      let nodeSymbol = this.nodeSymbolGroup
        .selectAll("path")
        .data(nodes, function(d) {
          return d.uuid;
        });
      nodeSymbol.exit().remove();
      const nodeSymbolEnter = this.drawNodeSymbol(nodeSymbol);
      nodeSymbol = nodeSymbolEnter.merge(nodeSymbol);
      nodeSymbol.attr("fill", d => {
        if (d.color) {
          return d.color;
        }
        return "#25BC9E";
      });
      nodeSymbol.attr("display", function(d) {
        if (typeof d.hasFile !== "undefined" && d.hasFile > 0) {
          return "block";
        }
        return "none";
      });
      this.simulation.nodes(nodes).on("tick", ticked);
      this.simulation.force("link").links(links);
      this.simulation.alphaTarget(1).restart();
      // 连线弯曲配置
      function linkArc(d) {
        const dx = d.target.x - d.source.x;
        const dy = d.target.y - d.source.y;
        const dr = Math.sqrt(dx * dx + dy * dy);
        const unevenCorrection = d.sameUneven ? 0 : 0.5;
        const curvature = 2;
        let arc =
          (1.0 / curvature) *
          ((dr * d.maxSameHalf) / (d.sameIndexCorrected - unevenCorrection));
        if (d.sameMiddleLink) {
          arc = 0;
        }
        const dd =
          "M" +
          d.source.x +
          "," +
          d.source.y +
          "A" +
          arc +
          "," +
          arc +
          " 0 0," +
          d.sameArcDirection +
          " " +
          d.target.x +
          "," +
          d.target.y;
        return dd;
      }
      const linkTextList = this.linkTextGroup.selectAll("g");
      const linkText = this.linkTextGroup.selectAll("g >text");
      // 监听布局，更新
      function ticked() {
        link.attr("d", linkArc);
        // 更新节点坐标
        node
          .attr("cx", function(d) {
            return d.x;
          })
          .attr("cy", function(d) {
            return d.y;
          });
        // 更新节点操作按钮组坐标
        nodeButton
          .attr("cx", function(d) {
            return d.x;
          })
          .attr("cy", function(d) {
            return d.y;
          });

        nodeButton.attr("transform", function(d) {
          return "translate(" + d.x + "," + d.y + ") scale(1)";
        });
        // 更新文字坐标
        nodeText
          .attr("x", function(d) {
            return d.x;
          })
          .attr("y", function(d) {
            return d.y;
          });
        // 更新回形针坐标
        nodeSymbol.attr("transform", function(d) {
          return "translate(" + (d.x + 8) + "," + (d.y - 30) + ") scale(1)";
        });
        linkText.attr("dy", 5);
        linkTextList.attr("transform", function(d) {
          if (d.target.x < d.source.x) {
            const bbox = this.getBBox();
            const rx = bbox.x + bbox.width / 2;
            const ry = bbox.y + bbox.height / 2;
            return "rotate(180 " + rx + " " + ry + ")";
          } else {
            return "rotate(360)";
          }
        });
      }
      // 配置缩放
      // 计算出最小和最大的X，Y
      // 去除拖拽跳动问题
      if (this.scale == null) {
        this.graph.nodes.filter(res => res.uuid);
        const xExtent = d3.extent(d3.values(this.graph.nodes), function(n) {
          return n.x;
        });
        const yExtent = d3.extent(d3.values(this.graph.nodes), function(n) {
          return n.y;
        });
        const configwidth = this.width;
        const configHeight = this.height;
        // （整个屏幕的大小-（最大X-最小X））= 2边空余大小。
        const trY = configwidth - [xExtent[1]];
        const trX = xExtent[0];
        const xty = configHeight - yExtent[1];
        const xtt = yExtent[0];
        // 计算整个图像高x和高y 和高宽比
        const scaleX = parseFloat((xExtent[1] - xExtent[0]) / configwidth);
        const scaleY = parseFloat((yExtent[1] - yExtent[0]) / configHeight);
        // 视觉舒服的缩放是0.8  所以 0.8= （高宽最大的）比例 *X 就得来  X= 0.8 * 比例
        const scale =
          parseFloat(0.7 / Math.max(scaleX, scaleY)) == "Infinity"
            ? 1
            : parseFloat(0.7 / Math.max(scaleX, scaleY));
        // 偏移量就是 2边空余大小除以2等于2边的大小都一样大，
        const translateX = trY - xExtent[0];
        const translateY = -xty;
        this.scale = scale;
        if (scale === 1) {
          this.svg.call(
            this.zoom.transform,
            d3.zoomIdentity.translate(0, 0).scale(scale)
          );
        } else {
          this.svg.call(
            this.zoom.transform,
            d3.zoomIdentity
              .translate(parseFloat(translateX * scale), translateY * scale)
              .scale(scale)
          );
        }
      }
      // 添加滚轮缩放
      this.svg.call(this.zoom);
      this.svg.on("dblclick.zoom", null); // 静止双击缩放
    },
    // 绘制节点按钮
    addNodeButton() {
      // 先删除所有为节点自定义的按钮组
      const _this = this;
      d3.selectAll("svg >defs").remove();
      const nodes = _this.graph.nodes;
      if (!_this.svg) return;
      const nodeButton = _this.svg.append("defs");
      nodes.forEach(function(m) {
        _this.createMenuButton(
          nodeButton,
          m,
          _this.ringFunction,
          0,
          _this.ringFunction.length,
          0
        );
      });
    },
    createMenuButton(
      nodeButton,
      m,
      menuItems,
      level,
      parentMenuLength,
      actionIndex
    ) {
      const _this = this;
      //构建按钮组所占大小，均分一个圆，每份占1，[1,1,1,1,1,1]
      let menuGroup = [];
      let other = 0.0;
      //最内层是没有菜单，不用用空白占位每个action
      if (level == 0) {
        for (let i = 0; i < parentMenuLength; i++) {
          menuGroup.push(1 / parentMenuLength);
        }
      } else {
        //当前0级菜单的索引，为0表示在首位，没有前置空白占位，大于0表示有前置菜单，角度是对应0及菜单的角度
        if (actionIndex > 0) {
          for (let i = 0; i < actionIndex; i++) {
            menuGroup.push(1 / parentMenuLength);
            let ratio = 1 / parentMenuLength;
            other += ratio;
          }
        }
        //当前1级菜单要分成几份，数组就有增加几个长度，数组长度=前置Action的占比+当前菜单本身的占比+补位的空白，三者共同生成一个圆，空白去掉前两者整体占比就会扩大，和0级菜单就会错位
        for (let j = 0; j < menuItems.length; j++) {
          let ratio = 1 / parentMenuLength / menuItems.length;
          menuGroup.push(ratio);
          other += ratio;
        }
        //空白的占比，占一个长度
        menuGroup.push(1 - other);
      }

      //先检查元素是否存在，避免重复画
      let out_circle = d3.select("#out_circle" + m.uuid);
      if (out_circle._groups[0][0] == null) {
        nodeButton.append("g").attr("id", "out_circle" + m.uuid);
      }
      let circle_menu = d3.select("#circle_menu_" + m.uuid + "_level_" + level);
      if (circle_menu._groups[0][0] == null) {
        circle_menu = d3
          .selectAll("#out_circle" + m.uuid)
          .append("g")
          .attr("id", "circle_menu_" + m.uuid + "_level_" + level);
      }
      //这里加sort去掉默认的排序，按照数组的顺序从12点方向顺时针画圆，不使用sort(null)默认按数字排序
      const pise = d3.pie().sort(null);
      const pisedata = pise(menuGroup);
      const buttonEnter = circle_menu
        .selectAll("#circle_menu_" + m.uuid + "_level_" + level)
        .data(pisedata)
        .enter()
        .append("g")
        .attr("cursor", "pointer")
        .attr("class", function(d, i) {
          const id =
            "menu_" +
            m.uuid +
            "_level_" +
            level +
            "_pAction_" +
            actionIndex +
            "_action_" +
            i;
          return id;
        });
      let innerR = parseInt(m.r) + 40 * level;
      let ountR = parseInt(m.r) + 40 * (level + 1);
      const arc = d3
        .arc()
        .innerRadius(innerR)
        .outerRadius(ountR);
      buttonEnter
        .append("path")
        .attr("d", function(d) {
          return arc(d);
        })
        .attr("fill", "#CCE4F7")
        .style("opacity", 1)
        .attr("stroke", "#ffffff")
        .attr("stroke-width", 2);
      menuItems.forEach((item, index) => {
        const defs = d3
          .selectAll("svg >defs")
          .selectAll("#circle_menu_" + m.uuid + "_level_" + level);
        if (item.icon.type == "url") {
          const catpattern = defs
            .append("pattern")
            .attr("id", "icon_" + m.uuid + "_level_" + level + "_" + index)
            .attr("height", 1)
            .attr("width", 1);
          catpattern
            .append("image")
            .attr("width", 30)
            .attr("height", 30)
            .attr("xlink:href", item.icon.content);
        } else if (item.icon.type == "icon") {
          const catpattern = defs
            .append("pattern")
            .attr("id", "icon_" + m.uuid + "_level_" + level + "_" + index)
            .attr("height", 1)
            .attr("width", 1);
          catpattern
            .append("use")
            .attr("width", 30)
            .attr("height", 30)
            .attr("xlink:href", "#" + item.icon.content);
        }
        if (item.icon.type == "url" || item.icon.type == "icon") {
          buttonEnter
            .append("circle")
            .attr("r", 15)
            .attr("transform", function(d) {
              return "translate(" + arc.centroid(d) + ")";
            })
            .attr("fill", function(d, i) {
              if (i == index + actionIndex) {
                return (
                  "url(#icon_" + m.uuid + "_level_" + level + "_" + index + ")"
                );
              } else {
                d3.select(this).remove();
              }
            });
        } else if (item.icon.type == "text") {
          buttonEnter
            .append("text")
            .attr("text-anchor", "middle")
            .attr("transform", function(d) {
              return "translate(" + arc.centroid(d) + ")";
            })
            .text(function(d, i) {
              if (i == index + actionIndex) {
                return item.title;
              } else {
                d3.select(this).remove();
              }
            })
            .attr("font-size", 10);
        }
        buttonEnter.on("click", function(d, i) {
          //console.log(d)
          let currentItem = menuItems[i-actionIndex];
          if (currentItem.childrens && currentItem.childrens.length > 0) {
            let levelGroup = "#circle_menu_" + m.uuid + "_level_" + (level + 1);
            d3.selectAll(levelGroup).style("display", "block");
            let btn="g[class^='menu_"+m.uuid+"_level_"+(level + 1)+"']";
            //console.log(btn)
             d3.selectAll(btn).style("display", "none");
              let selectBtn="g[class^='menu_"+m.uuid+"_level_"+(level + 1)+"_pAction_"+i+"']";
              //console.log(selectBtn)
             d3.selectAll(selectBtn).style("display", "block");
          } else {
            currentItem.defaultEvent(m, _this, d3);
          }
          d3.event.stopPropagation();
        });
        if (item.childrens && item.childrens.length > 0) {
          this.createMenuButton(
            nodeButton,
            m,
            item.childrens,
            level + 1,
            menuGroup.length,
            index
          );
        }
      });

      //按钮显示处理
      for (let i = 0; i < actionIndex; i++) {
        for (let j = 0; j < actionIndex; j++) {
          //menu_1_level_1_pAction_0_action_0
          let menuBtnClass =
            ".menu_" +
            m.uuid +
            "_level_" +
            level +
            "_pAction_" +
            actionIndex +
            "_action_" +
            j;
          //移除多余的按钮组
          d3.selectAll(menuBtnClass).remove();
        }
        let menuBtnClass2 =
          ".menu_" +
          m.uuid +
          "_level_" +
          level +
          "_pAction_" +
          actionIndex +
          "_action_" +
          (menuGroup.length - 1);
        //移除多余的按钮组
        d3.selectAll(menuBtnClass2).remove();
      }
      if (level > 0 && actionIndex == 0) {
        let menuBtnClass0 =
          ".menu_" +
          m.uuid +
          "_level_" +
          level +
          "_pAction_" +
          actionIndex +
          "_action_" +
          (menuGroup.length - 1);
        //移除多余的按钮组
        d3.selectAll(menuBtnClass0).remove();
      }

      if (level > 0) {
        let levelGroup = "#circle_menu_" + m.uuid + "_level_" + level;
        d3.selectAll(levelGroup).style("display", "none");
      }
    },
    // 节点拖动开始
    dragStarted(d) {
      if (!d3.event.active) this.simulation.alphaTarget(0.3).restart();
      d.x = d3.event.x;
      d.y = d3.event.y;
    },
    // 拖拽中
    dragged(d) {
      let vx = d3.event.x - d.x; //x轴偏移量
      let vy = d3.event.y - d.y; //y轴偏移量
      d.x = d3.event.x;
      d.y = d3.event.y;
      d.fx = d3.event.x;
      d.fy = d3.event.y;
      let targetNodeIds = this.graph.links
        .filter(n => n.sourceId == d.uuid)
        .map(m => m.targetId);
      if (targetNodeIds && targetNodeIds.length > 0) {
        targetNodeIds.forEach(x => {
          this.graph.nodes
            .filter(n => n.uuid == x)
            .map(m => {
              m.fx = m.fx + vx;
              m.fy = m.fy + vy;
              m.x = m.x + vx;
              m.y = m.y + vy;
              return m;
            });
        });
      }
    },
    dragEnded(d) {
      if (!d3.event.active) this.simulation.alphaTarget(0.3);
      let moveNodes = [];
      moveNodes.push({ uuid: d.uuid, fx: d.fx, fy: d.fy });
      let relevantNodes = this.graph.links.filter(n => n.sourceId == d.uuid);
      if (relevantNodes && relevantNodes.length > 0) {
        relevantNodes.forEach(x => {
          let targetNodes = this.graph.nodes
            .filter(n => n.uuid == x.targetId)
            .map(m => {
              let item = { uuid: m.uuid, fx: m.fx, fy: m.fy };
              return item;
            });
          moveNodes = moveNodes.concat(targetNodes);
        });
      }
      //console.log(moveNodes);
      //批量更新本次移动的节点坐标
      this.updateCoordinateOfNode(moveNodes);
      // 节点重叠菜单
      // const MinX = parseFloat(d.x) - 40;
      // const MaX = parseFloat(d.x) + 40;
      // const MinY = parseFloat(d.y) - 40;
      // const MaY = parseFloat(d.y) + 40;
      // this.graph.nodes.forEach(item => {
      //   if (
      //     MinX < item.x &&
      //     item.x < MaX &&
      //     MinY < item.y &&
      //     item.y < MaY &&
      //     item.id !== d.uuid
      //   ) {
      //     //节点重叠处理逻辑
      //     console.log("重叠了");
      //   }
      // });
    },
    // 绘制节点
    drawNode(node) {
      const _this = this;
      const gradient = node.enter().append("g");
      const nodeEnter = gradient.append("circle");
      const defs = gradient.append("defs").attr("id", function(d) {
        return "imgdef" + d.uuid;
      });
      const catpattern = defs
        .append("pattern")
        .attr("id", function(d) {
          return "catpattern" + d.uuid;
        })
        .attr("height", 1)
        .attr("width", 1);
      catpattern
        .append("image")
        .attr("width", d => d.r * 2)
        .attr("height", d => d.r * 2)
        .attr("xlink:href", function(d) {
          if (d.image) {
            if (d.image.indexOf("http") > -1) {
              return d.image;
            } else {
              return process.env.VUE_APP_BASE_API + d.image;
            }
          }
        });
      nodeEnter.attr("r", function(d) {
        return d.r ? parseInt(d.r) : 25;
      });
      nodeEnter
        .attr("fill", function(d) {
          if (d.image) {
            return "url(#catpattern" + d.uuid + ")";
          } else {
            if (d.color) {
              return d.color;
            }
            return "#21bb9e";
          }
        })
        .attr("class", d => {
          return "circle_" + d.uuid;
        });
      nodeEnter.style("opacity", 1);
      nodeEnter.style("stroke-opacity", 0.6);
      nodeEnter
        .append("title") // 为每个节点设置title
        .text(function(d) {
          return d.name;
        });
      nodeEnter.on("mouseenter", function(d) {
        const aa = d3.select(this)._groups[0][0];
        if (aa.classList.contains("selected")) return;
        d3.select(this)
          .style("stroke-width", "6")
          .style("stroke", "#1890ff")
          .style("opacity", 1);
      });
      nodeEnter.on("mouseleave", function(d) {
        const aa = d3.select(this)._groups[0][0];
        if (aa.classList.contains("selected")) return;
        d3.select(this).style("stroke-width", "0");
        d3.select(".node").style("fill-opacity", 1);
        d3.select(".nodeText").style("fill-opacity", 1);
        d3.selectAll("path[class^='Links_']").style("display", "block");
        d3.selectAll("text[class^='LinkText_']").style("display", "block");
      });
      nodeEnter.on("mouseover", function(d) {
        //todo鼠标放上去只显示相关节点，其他节点和连线隐藏
        d3.selectAll(".node").style("fill-opacity", 0.5);
        var relvantNodeIds = [];
        var relvantNodes = _this.graph.links.filter(function(n) {
          return n.sourceId == d.uuid || n.targetId == d.uuid;
        });
        relvantNodes.forEach(function(item) {
          relvantNodeIds.push(item.sourceId);
          relvantNodeIds.push(item.targetId);
        });
        //显示相关的节点
        _this.nodeGroup.selectAll("circle").style("fill-opacity", function(c) {
          if (relvantNodeIds.indexOf(c.uuid) > -1) {
            return 1.0;
          }
        });
        //透明所有节点文字
        d3.selectAll(".nodeText").style("fill-opacity", 0.5);
        //显示相关的节点文字
        _this.nodeTextGroup
          .selectAll("text")
          .style("fill-opacity", function(c) {
            if (relvantNodeIds.indexOf(c.uuid) > -1) {
              return 1.0;
            }
          });
        //透明所有连线
        //d3.selectAll('.line').style('stroke-opacity', 0.1)
        d3.selectAll("path[class^='Links_']").style("display", "block");
        //显示相关的连线
        _this.linkGroup
          .selectAll("path[class^='Links_']")
          .style("display", function(c) {
            if (c.lk.targetId === d.uuid || c.lk.sourceId === d.uuid) {
              return "block";
            }
            return "none";
          });
        //透明所有连线文字
        //d3.selectAll('.lineText').style('fill-opacity', 0.1)
        d3.selectAll("text[class^='LinkText_']").style("display", "none");
        //显示相关的连线文字
        _this.linkTextGroup
          .selectAll("text[class^='LinkText_']")
          .style("display", function(c) {
            if (c.lk.targetId === d.uuid || c.lk.sourceId === d.uuid) {
              return "block";
            }
            return "none";
          });
      });
      //dblclick 会触发两次单击，所以在click里设置定时timer来控制双击
      // nodeEnter.on("dblclick", function (d) {
      //   console.log("双击")
      //   d3.event.stopPropagation();
      //     d3.event.preventDefault();
      // });
      nodeEnter.on("click", function(d, i) {
        console.log("node click");
        _this.selectNode.id = d.uuid;
        _this.selectNode.cname = d.name;
        _this.svg.selectAll(".buttongroup").style("display", "block");
         d3.selectAll("g[id^='circle_menu_']").style("display", "none");
        let btn="g[id^='circle_menu_"+d.uuid+"_level_0']";
         d3.selectAll(btn).style("display", "block");
        //因为svg也有click事件，这里要阻止冒泡
        d3.event.stopPropagation();
      });
      nodeEnter.call(
        d3
          .drag()
          .on("start", this.dragStarted)
          .on("drag", this.dragged)
          .on("end", this.dragEnded)
      );
      return nodeEnter;
    },
    // 绘制节点文字
    drawNodeText(nodeText) {
      const _this = this;
      const nodeTextEnter = nodeText
        .enter()
        .append("g")
        .append("text")
        .style("fill", function(d) {
          if (d.image) {
            return "#000000";
          }
          return "#fff";
        })
        // .attr('dx', function(d){
        //   return -1*(parseInt(d.r)-10)
        // })//设置居中不用偏移
        .attr("dy", function(d) {
          if (d.image) {
            return d.r + 20; //文字放在节点外边
          }
          return 4; //文字是站在水平半径这条线上的，所以向下偏移一些，具体值应该是文字高度的一半
        })
        .attr("font-family", "微软雅黑")
        .attr("text-anchor", "middle"); //设置文字居中
      nodeTextEnter.text(function(d) {
        let text = d.name;
        const len = text.length;
        if (d.image) {
          return d.name;
        } else {
          //取圆的半径r，两边各空出5px,然后求出文字能放的最大长度(parseInt(d.r)-5)*2,一个文字占16px(系统默认font-size=16px),
          //相除得到最多能放多少汉字，font-size换算比有待考证，文字两边和圆边框的间距忽大忽小，有缘者来优化
          let dr = ((parseInt(d.r) - 5) * 2) / 16;
          if (dr < len) {
            return text.substring(0, dr) + "...";
          } else {
            return d.name;
          }
        }
      });
      nodeTextEnter.on("click", function(d, i) {
        _this.selectNode.id = d.uuid;
        _this.selectNode.cname = d.name;
        const out_buttongroup_id = ".out_buttongroup_" + d.uuid;
        _this.svg.selectAll(".buttongroup").style("display", "none");
        //_this.svg.selectAll(".buttongroup").classed("circle_none", true);
        _this.svg.selectAll(out_buttongroup_id).style("display", "block");
        //_this.svg.selectAll(out_buttongroup_id).classed("circle_none", false);
      });
      nodeTextEnter.call(
        d3
          .drag()
          .on("start", this.dragStarted)
          .on("drag", this.dragged)
          .on("end", this.dragEnded)
      );
      return nodeTextEnter;
    },
    // 给节点画上标识
    drawNodeSymbol(nodeSymbol) {
      const symbol_path =
        "M566.92736 550.580907c30.907733-34.655573 25.862827-82.445653 25.862827-104.239787 0-108.086613-87.620267-195.805867-195.577173-195.805867-49.015467 0-93.310293 18.752853-127.68256 48.564907l-0.518827-0.484693-4.980053 4.97664c-1.744213 1.64864-3.91168 2.942293-5.59104 4.72064l0.515413 0.484693-134.69696 133.727573L216.439467 534.8352l0 0 137.478827-136.31488c11.605333-10.410667 26.514773-17.298773 43.165013-17.298773 36.051627 0 65.184427 29.197653 65.184427 65.24928 0 14.032213-5.33504 26.125653-12.73856 36.829867l-131.754667 132.594347 0.515413 0.518827c-10.31168 11.578027-17.07008 26.381653-17.07008 43.066027 0 36.082347 29.16352 65.245867 65.184427 65.245867 16.684373 0 31.460693-6.724267 43.035307-17.07008l0.515413 0.512M1010.336427 343.49056c0-180.25472-145.882453-326.331733-325.911893-326.331733-80.704853 0-153.77408 30.22848-210.418347 79.0528l0.484693 0.64512c-12.352853 11.834027-20.241067 28.388693-20.241067 46.916267 0 36.051627 29.16352 65.245867 65.211733 65.245867 15.909547 0 29.876907-6.36928 41.192107-15.844693l0.38912 0.259413c33.624747-28.030293 76.301653-45.58848 123.511467-45.58848 107.99104 0 195.549867 87.6544 195.549867 195.744427 0 59.815253-27.357867 112.71168-69.51936 148.503893l0 0-319.25248 317.928107 0 0c-35.826347 42.2912-88.654507 69.710507-148.340053 69.710507-107.956907 0-195.549867-87.68512-195.549867-195.805867 0-59.753813 27.385173-112.646827 69.515947-148.43904l-92.18048-92.310187c-65.69984 59.559253-107.700907 144.913067-107.700907 240.749227 0 180.28544 145.885867 326.301013 325.915307 326.301013 95.218347 0 180.02944-41.642667 239.581867-106.827093l0.13312 0.129707 321.061547-319.962453-0.126293-0.13312C968.69376 523.615573 1010.336427 438.71232 1010.336427 343.49056L1010.336427 343.49056 1010.336427 343.49056zM1010.336427 343.49056"; // 定义回形针形状
      const nodeSymbolEnter = nodeSymbol
        .enter()
        .append("path")
        .attr("d", symbol_path);
      nodeSymbolEnter.call(
        d3
          .drag()
          .on("start", this.dragStarted)
          .on("drag", this.dragged)
          .on("end", this.dragEnded)
      );
      return nodeSymbolEnter;
    },
    // 构建节点环形按钮组
    drawNodeButton(nodeButton) {
      const nodeButtonEnter = nodeButton
        .enter()
        .append("g")
        .append("use") //  为每个节点组添加一个 use 子元素
        .attr("r", function(d) {
          return parseInt(d.r);
        })
        .attr("xlink:href", function(d) {
          return "#out_circle" + d.uuid;
        }) //  指定 use 引用的内容
        .attr("class", function(d, i) {
          return "buttongroup out_buttongroup_" + d.uuid;
        })
        .style('display','none')
        //.classed("circle_none", true);

      return nodeButtonEnter;
    },
    // 添加箭头
    addMaker() {
      const arrow_path = "M0,-5L10,0L0,5"; // 定义箭头形状
      const _this = this;
      _this.svg
        .append("marker")
        .attr("id", "arrow")
        .attr("markerUnits", "strokeWidth")
        .attr("markerWidth", "6") //
        .attr("markerHeight", "6")
        .attr("viewBox", "0 -5 10 10")
        .attr("refX", "37") // 13
        .attr("refY", "0")
        .attr("orient", "auto")
        .append("path")
        .attr("d", arrow_path)
        .attr("fill", "#fce6d4");
    },
    // 构建连线，绑定事件
    drawLink(link) {
      const _this = this;
      const linkEnter = link
        .enter()
        .append("path")
        .attr("pointer-events", "all")
        .attr("stroke-width", 1.5)
        .attr("stroke", "#FBB613") //'#FBB613'
        .attr("id", function(d) {
          return "invis_" + d.lk.uuid;
        })
        .attr("class", d => {
          return "Links_" + d.lk.uuid;
        })
        .attr("fill", "none")
        // 箭头
        .attr("marker-end", function(d) {
          let marker = "url(#arrow)";
          return marker;
        });
      // 连线鼠标滑入
      linkEnter.on("mouseenter", function(d) {
        _this.editLinkState = true;
        _this.editLink = d;
        d3.select(".Links_" + d.lk.uuid)
          .style("stroke-width", "10")
          .attr("stroke", "#e4e2e2")
          .attr("marker-end", "");
        _this.selectNode.uuid = d.lk.uuid;
        _this.selectNode.cname = d.lk.name;
        const e = window.event;
        const link = {
          left: e.pageX - 300,
          top: e.pageY - 120,
          show: true,
          sdata: _this.selectNode
        };
        _this.$refs.menu_link.init(link);
      });
      // 连线鼠标离开
      linkEnter.on("mouseleave", function(d) {
        _this.editLinkState = false;
        d3.select(".Links_" + d.lk.uuid)
          .style("stroke-width", 1.5)
          .attr("stroke", d => {
            if (d.color) {
              return d.color;
            }
            return "#FBB613";
          })
          .attr("marker-end", d => {
            return "url(#arrow)";
          });
      });
      //可以调用call方法处理连线的动作
      return linkEnter;
    },
    // 构建连线上的文字，并绑定事件
    drawLinkText(links) {
      const _this = this;
      const linkTextEnter = links
        .enter()
        .append("g")
        .attr("class", function(d) {
          return "TextLink_" + d.lk.uuid;
        });
      linkTextEnter
        .append("text")
        .attr("class", function(d) {
          return "LinkText_" + d.lk.uuid;
        })
        .append("textPath")
        .attr("filter", "url(#Linktext)")
        .attr("startOffset", "50%")
        .attr("text-anchor", "middle")
        .attr("xlink:href", function(d) {
          return "#invis_" + d.lk.uuid;
        })
        .style("font-family", "SimSun")
        .style("fill", "#434343")
        .style("stroke", "#434343")
        .style("font-size", 13)
        .text(function(d) {
          return d.lk.name;
        });
      // 连线鼠标滑入
      linkTextEnter.on("mouseenter", function(d) {
        const e = window.event;
        _this.selectNode.uuid = d.lk.uuid;
        _this.selectNode.cname = d.lk.name;
        const link = {
          left: e.pageX - 300,
          top: e.pageY - 120,
          show: true,
          sdata: _this.selectNode
        };
        _this.$refs.menu_link.init(link);
      });
      linkTextEnter.on("click", function(d) {
        console.log("连线文字点击");
      });
      const linkTextSS = linkTextEnter.insert("filter", "text");
      const linkTextSQ = linkTextSS
        .attr("id", "Linktext")
        .attr("height", "110%")
        .attr("width", "110%");
      linkTextSQ
        .append("feFlood")
        .attr("flood-color", "#ffffff")
        .attr("flood-opacity", 1);
      linkTextSQ
        .append("feComposite")
        .attr("in", "SourceGraphic")
        .attr("in2", "floodFill");
      return linkTextSQ;
    },
    // 连线点击框--编辑
    editLinkName() {
      this.$emit("editLinkName", d3, this);
    },
    // 连线点击框--删除连线
    deleteLinkName() {
      this.$emit("deleteLinkName", d3, this);
    },
    changeCursor() {
      d3.select(".BOX-SVG").style("cursor", "crosshair"); //进入新增模式，鼠标变成＋
    }
  }
};
</script>

<style scoped lang="scss">
.knowledge-BOX {
  width: 100%;
  height: 100%;
}
.SVG {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
}

text {
  cursor: pointer;
  max-width: 30px;
  display: inline-block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  vertical-align: middle;
}
circle {
  cursor: pointer;
}

.circle_none {
  display: none;
}
.nodetext {
  font-size: 12px;
  font-family: SimSun;
  fill: #000000;
}
.sase {
  background: #ffffff;
}
</style>
