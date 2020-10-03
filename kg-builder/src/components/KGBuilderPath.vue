<template>
  <div>
    <div id="gid_tc" style="float:left;">
      <div id="gid"></div>
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
            <a href="javascript:;" @click="zoomIn"
              ><span><i class="el-icon-zoom-in"></i>放大</span></a
            >
          </li>
          <li>
            <a href="javascript:;" @click="zoomOut"
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
              @click="exitFullScreen"
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
import axios from 'axios'
import * as d3 from 'd3'
import $ from 'jquery'

export default {
  props: ['pid'],
  data() {
    return {
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
      qaGraphNode: {},
      qaButtonGroup: {},
      qaGraphNodeText: {},
      qaGraphLink: {},
      qaGraphLinkText: {},
      graph: {
        nodes: [],
        links: [],
      },
      defaultR: 30,
      colorList: [
        '#ff8373',
        '#f9c62c',
        '#a5ca34',
        '#6fce7a',
        '#70d3bd',
        '#ea91b0',
      ],
      pagesizelist: [
        { size: 100, isactive: false },
        { size: 300, isactive: false },
        { size: 500, isactive: true },
        { size: 1000, isactive: false },
      ],
      toolbarData: [
        { name: '编辑', value: 1, code: 'edit' },
        { name: '展开', value: 1, code: 'more' },
        { name: '追加', value: 1, code: 'append' },
        { name: '连线', value: 1, code: 'link' },
        { name: '删除', value: 1, code: 'delete' },
      ],
      selectUuid: 0,
      nodeRecordList: [],
    }
  },
  components: {},
  mounted() {
    this.initGraphContainer()
    this.addMaker()
    this.initGraph()
  },
  created() {},
  watch: {},
  methods: {
    initGraphContainer() {
      this.gcontainer = d3.select('#gid')
      if (this.isFullscreen) {
        this.width = window.screen.width
        this.height = window.screen.height
      } else {
        this.width = $('#' + this.pid).width()
        this.height = $('#' + this.pid).height()
      }
      this.svg = this.gcontainer.append('svg')
      var sWidth = this.width
      var sHeight = this.height
      this.svg.attr('width', sWidth)
      this.svg.attr('height', sHeight)
      // this.svg.attr("viewBox", "0 0 " + sWidth / 2 + " " + sHeight / 2);
      this.svg.attr('id', 'svg_idx')
      this.svg.attr('preserveAspectRatio', 'xMidYMidmeet')
      this.simulation = d3
        .forceSimulation()
        .force('charge', d3.forceManyBody().strength(-1500))
        .force(
          'link',
          d3
            .forceLink()
            .distance(60)
            .id(function (d) {
              return d.uuid
            })
        )
        .force('collide', d3.forceCollide().strength(-30))
        .force('center', d3.forceCenter(this.width / 2, this.height / 2))
      this.qaGraphLink = this.svg.append('g').attr('class', 'line')
      this.qaGraphLinkText = this.svg.append('g').attr('class', 'linetext')
      this.qaGraphNode = this.svg.append('g').attr('class', 'node')
      this.qaGraphNodeText = this.svg.append('g').attr('class', 'nodetext')
      this.nodebuttonGroup = this.svg.append('g').attr('class', 'nodebutton')
      this.svg.on(
        'click',
        function () {
          d3.selectAll('.buttongroup').classed('notshow', true)
        },
        false
      )
    },
    initGraph() {
      var _this = this
      axios.get('/static/kgData2.json', {}).then(function (response) {
        var data = response.data
        _this.graph.nodes = data.node
        _this.graph.links = data.relationship
        _this.updateGraph()
      })
    },
    addMaker() {
      var arrowMarker = this.svg
        .append('marker')
        .attr('id', 'arrow')
        .attr('markerUnits', 'strokeWidth')
        .attr('markerWidth', '12') //
        .attr('markerHeight', '12')
        .attr('viewBox', '0 0 12 12')
        .attr('refX', '38')
        .attr('refY', '6')
        .attr('orient', 'auto')
      var arrowPath = 'M2,2 L10,6 L2,10 L6,6 L2,2' // 定义箭头形状
      arrowMarker.append('path').attr('d', arrowPath).attr('fill', '#ccc')
    },
    openNode() {
      var _this = this
      var noddd = [
        {
          flag: '1',
          code: '27301111',
          parentCode: '273',
          grade: '2',
          name: '儒家2',
          uuid: '4617858011',
        },
        {
          code: '273012222',
          flag: '1',
          parentCode: '273',
          grade: '3',
          name: '故事轶闻2',
          uuid: '2636501111',
        },
      ]
      var newships = [
        {
          sourceid: '273',
          targetid: '2636501111',
          name: '',
          targetcode: '2730107',
          uuid: '91804213',
          sourcecode: '27301',
        },
        {
          sourceid: '273',
          targetid: '4617858011',
          name: '',
          targetcode: '273010723',
          uuid: '91804389',
          sourcecode: '2730107',
        },
      ]
      _this.graph.nodes = _this.graph.nodes.concat(noddd)
      _this.graph.links = _this.graph.links.concat(newships)
      _this.updategraph()
    },
    drawNode(nodes) {
      var _this = this
      var node = this.qaGraphNode.selectAll('circle').data(nodes, function (d) {
        return d.uuid
      })
      node.exit().remove()
      var nodeEnter = node.enter().append('circle')
      nodeEnter.on('click', function (d) {
        _this.selectUuid = d.uuid
        var out_buttongroup_id = '.out_buttongroup_' + d.uuid
        var selectItem = d3.select(out_buttongroup_id)._groups[0][0]
        if (selectItem.classList.contains('notshow')) {
          _this.svg.selectAll('.buttongroup').classed('notshow', true)
          d3.select(out_buttongroup_id).classed('notshow', false)
        } else {
          d3.select(out_buttongroup_id).classed('notshow', true)
        }
        event.stopPropagation()
      })
      nodeEnter.on('dblclick', function (d) {
        console.log(d)
        event.preventDefault()
      })
      nodeEnter.on('mouseenter', function () {
        d3.select(this).style('stroke-width', '6')
      })
      nodeEnter.on('mouseleave', function () {
        d3.select(this).style('stroke-width', 2)
        //todo其他节点和连线一并显示
        d3.select('.node').style('fill-opacity', 1)
        d3.select('.nodetext').style('fill-opacity', 1)
        d3.selectAll('.link').style('stroke-opacity', 1)
        d3.selectAll('.linktext').style('fill-opacity', 1)
      })
      nodeEnter.on('mouseover', function (d) {
        //todo鼠标放上去只显示相关节点，其他节点和连线隐藏
        d3.selectAll('.node').style('fill-opacity', 0.1)
        var relvantNodeIds = []
        var relvantNodes = _this.graph.links.filter(function (n) {
          return n.sourceid == d.uuid || n.targetid == d.uuid
        })
        relvantNodes.forEach(function (item) {
          relvantNodeIds.push(item.sourceid)
          relvantNodeIds.push(item.targetid)
        })
        //显示相关的节点
        _this.qaGraphNode
          .selectAll('circle')
          .style('fill-opacity', function (c) {
            if (relvantNodeIds.indexOf(c.uuid) > -1) {
              return 1.0
            }
          })
        //透明所有节点文字
        d3.selectAll('.nodetext').style('fill-opacity', 0.1)
        //显示相关的节点文字
        _this.qaGraphNodeText
          .selectAll('text')
          .style('fill-opacity', function (c) {
            if (relvantNodeIds.indexOf(c.uuid) > -1) {
              return 1.0
            }
          })
        //透明所有连线
        d3.selectAll('.line').style('stroke-opacity', 0.1)
        //显示相关的连线
        _this.qaGraphLink
          .selectAll('.link')
          .style('stroke-opacity', function (c) {
            if (c.lk.targetid === d.uuid || c.lk.sourceid === d.uuid) {
              console.log(c)
              return 1.0
            }
          })
        //透明所有连线文字
        d3.selectAll('.linetext').style('fill-opacity', 0.1)
        //显示相关的连线文字
        _this.qaGraphLinkText
          .selectAll('.linktext')
          .style('fill-opacity', function (c) {
            if (c.lk.targetid === d.uuid || c.lk.sourceid === d.uuid) {
              return 1.0
            }
          })
      })
      nodeEnter.call(
        d3
          .drag()
          .on('start', _this.dragStarted)
          .on('drag', _this.dragged)
          .on('end', _this.dragEnded)
      )
      node = nodeEnter.merge(node).text(function (d) {
        return d.name
      })
      node.style('stroke', function (d) {
        if (d.color) {
          return d.color
        }
        return '#A4ED6C'
      })
      node.style('stroke-opacity', 0.6)
      node.attr('r', function (d) {
        if (d.r) {
          return d.r
        }
        return d.index === 0 ? 28 : 20
      })
      node.attr('fill', function (d, i) {
        //创建圆形图像
        if (d.imgsrc) {
          var img_w = 77,
            img_h = 80
          var defs = d3.selectAll('svg >defs')
          var catpattern = defs
            .append('pattern')
            .attr('id', 'catpattern' + i)
            .attr('height', 1)
            .attr('width', 1)
          catpattern
            .append('image')
            .attr('x', -(img_w / 2 - d.r))
            .attr('y', -(img_h / 2 - d.r))
            .attr('width', img_w)
            .attr('height', img_h)
            .attr('xlink:href', d.imgsrc)
          return 'url(#catpattern' + i + ')'
        } else {
          if (d.cur === '1') {
            return _this.colorList[0]
          } else {
            return _this.colorList[2]
          }
        }
      })
      node
        .append('title') // 为每个节点设置title
        .text(function (d) {
          if (d.name) {
            return d.name
          }
          return ''
        })
      return node
    },
    drawNodeText(nodes) {
      var _this = this
      var nodetext = this.qaGraphNodeText
        .selectAll('text')
        .data(nodes, function (d) {
          return d.uuid
        })
      nodetext.exit().remove()
      var nodetextEnter = nodetext.enter().append('text')
      nodetextEnter.call(
        d3
          .drag()
          .on('start', _this.dragStarted)
          .on('drag', _this.dragged)
          .on('end', _this.dragEnded)
      )
      nodetext = nodetextEnter.merge(nodetext).text(function (d) {
        return d.name
      })
      nodetext
        .style('fill', function () {
          return '#333'
        })
        .attr('class', 'nodetext')
        .attr('dy', '3.6em')
        .attr('font-family', '宋体')
        .attr('font-size', 16)
        .attr('text-anchor', 'middle')
        .text(function (d) {
          return d.name
        })
      nodetext
        .append('title') // 为每个节点设置title
        .text(function (d) {
          if (d.name) {
            return d.name
          }
          return ''
        })
      return nodetext
    },
    drawLink(links) {
      var _this = this
      var link = this.qaGraphLink.selectAll('.link').data(links, function (d) {
        return d.uuid
      })
      link.exit().remove()
      var linkEnter = link
        .enter()
        .append('path')
        .attr('class', 'link')
        .attr('stroke-width', 1)
        .attr('stroke', function () {
          return _this.colorList[2]
        })
        .attr('fill', 'none')
        .attr('id', function (d) {
          return (
            'invis_' + d.lk.sourceid + '-' + d.lk.name + '-' + d.lk.targetid
          )
        })
        .attr('marker-end', 'url(#arrow)') // 箭头
      link = linkEnter.merge(link)
      return link
    },
    drawLinkText(links) {
      var _this = this
      var linktext = _this.qaGraphLinkText
        .selectAll('.linktext')
        .data(links, function (d) {
          return d.uuid
        })
      linktext.exit().remove()
      var linktextEnter = linktext
        .enter()
        .append('text')
        .attr('class', 'linktext')
        .append('textPath')
        .attr('startOffset', '50%')
        .attr('text-anchor', 'middle')
        .attr('xlink:href', function (d) {
          return (
            '#invis_' + d.lk.sourceid + '-' + d.lk.name + '-' + d.lk.targetid
          )
        })
        .style('fill', '#875034')
        .style('font-size', '10px')
        .text(function (d) {
          return d.lk.name
        })
      linktext = linktextEnter.merge(linktext).text(function (d) {
        return d.lk.name
      })
      return linktext
    },
    drawButtonGroup(nodes) {
      var _this = this
      d3.selectAll('.nodebutton >g').remove()
      var nodebutton = _this.nodebuttonGroup
        .selectAll('.nodebutton')
        .data(nodes, function (d) {
          return d.uuid
        })
      nodebutton.exit().remove()
      var nodebuttonEnter = nodebutton
        .enter()
        .append('use') //  为每个节点组添加一个 use 子元素
        .attr('r', function (d) {
          if (!d.r) {
            return _this.defaultR
          }
          return d.r
        })
        .attr('uuid', function (d) {
          return d.uuid
        })
        .attr('xlink:href', function (d) {
          if (!d.r) {
            return '#out_circle_' + _this.defaultR
          }
          return '#out_circle_' + d.r
        }) //  指定 use 引用的内容
        .attr('class', function (d) {
          return 'buttongroup out_buttongroup_' + d.uuid
        })
        .classed('notshow', true)
      nodebutton = nodebuttonEnter.merge(nodebutton)
      return nodebutton
    },
    drawToolButton() {
      var _this = this
      //先删除所有为节点自定义的按钮组
      d3.selectAll('svg >defs').remove()
      var nodes = _this.graph.nodes
      var pie = d3.pie().value(function (d) {
        return d.value //处理数据，指定value作为计算比例的字段
      })
      var piedata = pie(_this.toolbarData)
      var nodeButtonGroup = this.svg.append('defs')
      var nodeRArr = []
      nodes.forEach(function (m) {
        if (!m.r) {
          m.r = _this.defaultR
        }
        //按半径分别定义每种按钮组的图标
        if (nodeRArr.indexOf(m.r) == -1) {
          nodeRArr.push(m.r)
          var nbtng = nodeButtonGroup
            .append('g')
            .attr('id', 'out_circle_' + m.r) //为每一个节点定制一个按钮组，在画按钮组的时候为其指定该id
          var buttonGroupEnter = nbtng
            .selectAll('.buttongroup')
            .data(piedata)
            .enter()
            .append('g')
            .attr('class', function (d) {
              return 'action_' + d.data.code
            })
          var arc = d3
            .arc()
            .innerRadius(m.r)
            .outerRadius(m.r + 30)
          buttonGroupEnter
            .append('path')
            .attr('d', function (d) {
              return arc(d)
            })
            .attr('fill', '#E6A23C')
            .style('opacity', 0.6)
            .attr('stroke', '#6CB7ED')
            .attr('stroke-width', 1)
          buttonGroupEnter
            .append('text')
            .attr('transform', function (d) {
              return 'translate(' + arc.centroid(d) + ')'
            })
            .attr('text-anchor', 'middle')
            .text(function (d) {
              return d.data.name
            })
            .style('fill', function () {
              return '#fff'
            })
            .attr('font-size', 10)
        }
      })
    },
    bindEventButtonGroup() {
      var _this = this
      //按钮组事件绑定
      _this.toolbarData.forEach(function (m) {
        var btnClass = '.action_' + m.code
        _this.svg.selectAll(btnClass).on('click', function (d) {
          console.log(
            d.data.name + ':' + d.data.code + ':uuid:' + _this.selectUuid
          )
        })
      })
    },
    formatData() {
      var _this = this
      var lks = _this.graph.links
      var nodes = _this.graph.nodes
      nodes.forEach(function (n) {
        if (n.center === 1 || n.center === '1') {
          n.fx = _this.width / 2
          n.fy = _this.height / 2
        }
        if (typeof n.fx === 'undefined' || n.fx === '') {
          n.fx = null
        }
        if (typeof n.fy === 'undefined' || n.fy === '') {
          n.fy = null
        }
      })
      var links = []
      lks.forEach(function (m) {
        var sourceNode = nodes.filter(function (n) {
          return n.uuid === m.sourceid
        })[0]
        if (!sourceNode) return
        var targetNode = nodes.filter(function (n) {
          return n.uuid === m.targetid
        })[0]
        if (!targetNode) return
        links.push({ source: sourceNode.uuid, target: targetNode.uuid, lk: m })
      })
      var data = {}
      data.nodes = nodes
      data.links = links
      return data
    },
    formatLinkAttr(links) {
      var formatLinks = []
      //每两个节点间的连线，不管正向反向，全算一组，每组连线分别标上序号linkGroupIndex，计算每组连线总条数groupLinkTotal
      links.forEach(function (item) {
        var linkGroup = []
        //正向
        var forwardItems = links.filter(function (sItem) {
          return sItem.source == item.source && sItem.target == item.target
        })
        //反向
        var reverseItems = links.filter(function (sItem) {
          return sItem.source == item.target && sItem.target == item.source
        })
        linkGroup = linkGroup.concat(forwardItems)
        linkGroup = linkGroup.concat(reverseItems)
        var groupLinkTotal = forwardItems.length + reverseItems.length
        linkGroup.forEach(function (it, i) {
          //本组连线序号
          it.linkGroupIndex = i + 1
          it.groupLinkTotal = groupLinkTotal
        })
        formatLinks = formatLinks.concat(linkGroup)
      })
      formatLinks.forEach(function (item) {
        //线的单双，单数且小于2，则只有一条直线，大于2且单数，有多条弧线和一条直线
        var groupLinkTotalHalf = item.groupLinkTotal / 2
        //能否被2整除，是否有余数，true=有，false=没有，有余数必然有一条直线
        item.groupLinkUneven = item.groupLinkTotal % 2 !== 0
        //判断是否是最中间的直线（有余数，并且序号刚好是本组连线的一版，这里向上取整）
        item.groupLinkMiddle =
          item.groupLinkUneven === true &&
          Math.ceil(groupLinkTotalHalf) === item.linkGroupIndex
        //假设有一条直线，index刚好是groupLinkTotalHalf的值，上方的曲线都为0，下方的曲线都为1
        var groupLinkLowerHalf = item.linkGroupIndex <= groupLinkTotalHalf
        //上方的曲线和直线方向一致，下方的曲线和直线的方向相反
        //item.groupLinkArcDirection = groupLinkLowerHalf ? 0 : 1
        item.groupLinkArcDirection = 1
        //都在直线上方，则取当前序号，否则减去中间直线取序号
        item.groupLinkIndexCorrected = groupLinkLowerHalf
          ? item.linkGroupIndex
          : item.linkGroupIndex - Math.ceil(groupLinkTotalHalf)
        item.groupLinkMaxHalf = Math.round(item.groupLinkTotal / 2)
      })
      return formatLinks
    },
    updateGraph() {
      var _this = this
      var data = _this.formatData()
      var nodes = data.nodes
      var links = _this.formatLinkAttr(data.links)
      //定义按钮组引用的use元素
      _this.drawToolButton(nodes)
      // 更新节点
      var graphNode = _this.drawNode(nodes)
      // 更新节点文字
      var graphNodeText = _this.drawNodeText(nodes)
      // 更新按钮组
      var graphNodeButtonGroup = _this.drawButtonGroup(nodes)
      // 更新连线 links
      var graphLink = _this.drawLink(links)
      // 更新连线文字
      var graphLinkText = _this.drawLinkText(links)
      _this.simulation
        .nodes(nodes)
        .alphaTarget(0)
        .alphaDecay(0.05)
        .on('tick', ticked)
      function ticked() {
        // 更新连线坐标
        graphLink.attr('d', function (d) {
          var dx = d.target.x - d.source.x,
            dy = d.target.y - d.source.y,
            dr = Math.sqrt(dx * dx + dy * dy),
            unevenCorrection = d.groupLinkUneven ? 0 : 0.2
          var curvature = 2,
            arc =
              (1.0 / curvature) *
              ((dr * d.groupLinkMaxHalf) /
                (d.groupLinkIndexCorrected - unevenCorrection))
          if (d.groupLinkMiddle) {
            arc = 0
          }
          var dd =
            'M' +
            d.source.x +
            ',' +
            d.source.y +
            'A' +
            arc +
            ',' +
            arc +
            ' 0 0,' +
            d.groupLinkArcDirection +
            ' ' +
            d.target.x +
            ',' +
            d.target.y
          return dd
        })
        // 刷新连接线上的文字位置
        graphLinkText
          .attr('x', function (d) {
            if (!d.source.x || !d.target.x) return 0
            var x = (parseFloat(d.source.x) + parseFloat(d.target.x)) / 2
            return x
          })
          .attr('y', function (d) {
            if (!d.source.y || !d.target.y) return 0
            var y = (parseFloat(d.source.y) + parseFloat(d.target.y)) / 2
            return y
          })
        // 更新节点坐标
        graphNode
          .attr('cx', function (d) {
            return d.x
          })
          .attr('cy', function (d) {
            return d.y
          })
        // 更新节点操作按钮组坐标
        graphNodeButtonGroup
          .attr('cx', function (d) {
            return d.x
          })
          .attr('cy', function (d) {
            return d.y
          })
          .attr('transform', function (d) {
            return 'translate(' + d.x + ',' + d.y + ') scale(1)'
          })
        // 更新文字坐标
        graphNodeText
          .attr('x', function (d) {
            return d.x
          })
          .attr('y', function (d) {
            return d.y
          })
      }
      _this.simulation.force('link').links(links)
      _this.simulation.force(
        'center',
        d3.forceCenter(_this.width / 2, _this.height / 2)
      )
      _this.simulation.alpha(1).restart()
      // 鼠标滚轮缩放
      _this.zoom = d3.zoom().scaleExtent([0.1, 4]).on('zoom', _this.zoomed)
      _this.svg.call(_this.zoom)
      // 静止双击缩放
      _this.svg.on('dblclick.zoom', null)
      //为按钮组绑定事件
      _this.bindEventButtonGroup()
    },
    dragStarted(d) {
      if (!d3.event.active) this.simulation.alphaTarget(0.8).restart()
      d.fx = d.x
      d.fy = d.y
    },
    dragged(d) {
      d.fx = d3.event.x
      d.fy = d3.event.y
    },
    dragEnded(d) {
      if (!d3.event.active) this.simulation.alphaTarget(0)
      d.fx = d3.event.x
      d.fy = d3.event.y
    },
    zoomed() {
      d3.selectAll('.node').attr('transform', d3.event.transform)
      d3.selectAll('.nodetext text').attr('transform', d3.event.transform)
      d3.selectAll('.line').attr('transform', d3.event.transform)
      d3.selectAll('.linetext text').attr('transform', d3.event.transform)
      d3.selectAll('.nodebutton').attr('transform', d3.event.transform)
      //_this.svg.selectAll("g").attr("transform", d3.event.transform);
    },
    zoomClick(direction) {
      var self = this
      var factor = 0.2
      var targetZoom = 1
      var extent = self.zoom.scaleExtent()
      targetZoom = 1 + factor * direction
      if (targetZoom < extent[0] || targetZoom > extent[1]) {
        return false
      }
      self.zoom.scaleBy(self.svg, targetZoom) // 执行该方法后 会触发zoom事件
    },
    zoomIn() {
      this.zoomClick(1)
    },
    zoomOut() {
      this.zoomClick(-1)
    },
    refresh() {
      this.svg.call(this.zoom.transform, d3.zoomIdentity)
    },
    showFull() {
      this.isFullscreen = !this.isFullscreen
      var full = document.getElementById('kg_container')
      this.fullScreen(full)
    },
    fullScreen(element) {
      if (element.requestFullscreen) {
        element.requestFullscreen()
      } else if (element.mozRequestFullScreen) {
        element.mozRequestFullScreen()
      } else if (element.webkitRequestFullscreen) {
        element.webkitRequestFullscreen()
      } else if (element.msRequestFullscreen) {
        element.msRequestFullscreen()
      }
    },
    exitFullScreen() {
      this.isFullscreen = !this.isFullscreen
      if (document.exitFullscreen) {
        document.exitFullscreen()
      } else if (document.mozCancelFullScreen) {
        document.mozCancelFullScreen()
      } else if (document.webkitExitFullscreen) {
        document.webkitExitFullscreen()
      }
    },
    btnCollapseNode() {},
    btnOpenNode() {},
    close() {},
  },
}
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
.notshow {
  display: none;
}
.nodetext {
  pointer-events: all;
  cursor: pointer;
  stroke-dasharray: 0 0 0 0;
  stroke-dashoffset: 10;
  transition: all ease 0.1s;
}
.nodetext:hover {
  stroke-dashoffset: 0;
  stroke-dasharray: 100;
}
</style>
