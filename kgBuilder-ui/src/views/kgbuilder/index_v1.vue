<!--
 * @Description: kgBuilder
 * @Author: tanc
 * @Date: 2021-12-26 16:50:07
 * @LastEditors: Please set LastEditors
 * @LastEditTime: 2022-03-29 11:23:25
-->
<template>
  <div class="mind-box">
    <!-- 左侧 -->
    <el-scrollbar class="mind-l">
      <div class="ml-m">
        <h2 class="ml-ht">图谱列表</h2>
        <el-button
          type="info"
          style="margin: 2px 0 4px 2px;"
          plain
          size="small"
          @click="createDomain"
        >新建图谱</el-button>
        <div
          class="ml-a-box"
          style="min-height:280px"
        >
          <a
            @click="matchDomainGraph(m, $event)"
            v-for="(m, index) in pageModel.nodeList"
            :key="index"
            href="javascript:void(0)"
            :title="m.name"
          >
            <el-tag
              v-if="m.commend == 0"
              closable
              style="margin:2px"
              @close="deleteDomain(m.id, m.name)"
            >{{ m.name }}</el-tag>
          </a>
        </div>
        <div class="fr">
          <a
            href="javascript:void(0)"
            class="svg-a-sm"
            v-show="pageModel.pageIndex > 1"
            @click="prev"
          >上一页</a>
          <a
            href="javascript:void(0)"
            class="svg-a-sm"
            v-show="pageModel.pageIndex < pageModel.totalPage"
            @click="next"
          >下一页</a>
        </div>
        <!-- 关注及交流 -->
        <div>
          <kg-focus ref="kg_focus"></kg-focus>
        </div>
      </div>
    </el-scrollbar>
    <!-- 左侧over -->
    <!-- 右侧 -->
    <div class="mind-con">
      <!-- 头部工具栏 -->
      <div class="mind-top clearfix">
        <div
          v-show="domain != ''"
          class="fl"
          style="display: flex"
        >
          <div class="search">
            <el-button @click="getDomainGraph(0)">
              <svg
                class="icon"
                aria-hidden="true"
              >
                <use xlink:href="#icon-search"></use>
              </svg>
            </el-button>
            <el-input
              placeholder="请输入关键词"
              v-model="nodeName"
              @keyup.enter.native="getDomainGraph"
            ></el-input>
          </div>
          <span>
            <span class="dibmr">
              <span>显示节点个数:</span>
              <a
                v-for="(m, index) in pageSizeList"
                :key="index"
                @click="setMatchSize(m)"
                :title="m.size"
                href="javascript:void(0)"
                :class="[m.isActive ? 'sd-active' : '', 'sd']"
              >{{ m.size }}</a>
            </span>
          </span>
        </div>
        <div class="fr">
          <a
            href="javascript:void(0)"
            @click="showJsonData"
            class="svg-a-sm"
          >
            <i class="el-icon-tickets">查看数据</i>
          </a>

          <a
            href="javascript:void(0)"
            @click="saveImage"
            class="svg-a-sm"
          >
            <i class="el-icon-camera-solid">截图</i>
          </a>
          <a
            href="javascript:void(0)"
            @click="importGraph"
            class="svg-a-sm"
          >
            <i class="el-icon-upload">导入</i>
          </a>
          <a
            href="javascript:void(0)"
            @click="exportGraph"
            class="svg-a-sm"
          >
            <i class="el-icon-download">导出</i>
          </a>
          <a
            href="javascript:void(0)"
            @click="requestFullScreen"
            class="svg-a-sm"
          >
            <i class="el-icon-monitor">全屏</i>
          </a>
          <a
            href="javascript:void(0)"
            @click="help"
            class="svg-a-sm"
          >
            <i class="el-icon-info">帮助</i>
          </a>
          <a
            href="javascript:void(0)"
            @click="wanted"
            class="svg-a-sm"
          >
            <i class="el-icon-question">反馈</i>
          </a>
        </div>
      </div>
      <!-- 头部over -->
      <!-- 中部 -->
      <el-scrollbar
        class="mind-cen"
        id="graphcontainerdiv"
      >
        <div
          id="nodeDetail"
          class="node_detail"
        >
          <h5>详细数据</h5>
          <span
            class="node_pd"
            v-for="(m, k) in nodeDetail"
            :key="k"
          >{{ k }}:{{ m }}</span>
        </div>
        <!-- 中部图谱画布 -->
        <div
          id="graphContainer"
          class="graphContainer"
          @click="initContainerLeftClick"
          @contextmenu.prevent="initContainerRightClick"
        >
          <kgbuilder2 ref="kg_builder"
            :styles="style"
            :initData="graphData"
            :domain="domain"
            :domainId="domainId"
            :methods-properties="MethodsProperties"
            :link-color="LinkColor"
            :node-color="NodeColor"
            :ring-function="RingFunction"
            @editForm="editForm"
          />
        </div>
      </el-scrollbar>
      <!-- 中部over -->
      <div class="svg-set-box"></div>
      <!-- 底部 -->

      <!-- 底部over -->
    </div>
    <!-- 右侧over -->
    <!-- 空白处右键 -->
    <div>
      <menu-blank
        ref="menu_blank"
        @btnAddSingle="btnAddSingle"
        @btnQuickAddNode="btnQuickAddNode"
      ></menu-blank>
    </div>
    <!-- 连线按钮组 -->
    <div>
      <menu-link
        ref="menu_link"
        @updateLinkName="updateLinkName"
        @deleteLink="deleteLink"
      ></menu-link>
    </div>
    <!--编辑窗口-->
    <div>
      <kg-form
        ref="kg_form"
        @batchCreateNode="batchCreateNode"
        @batchCreateChildNode="batchCreateChildNode"
        @batchCreateSameNode="batchCreateSameNode"
        @createNode="createNode"
        @initNodeImage="initNodeImage"
        @initNodeContent="initNodeContent"
        @getDomain="getDomain"
      >
      </kg-form>
    </div>
    <!-- 富文本展示 -->
    <div>
      <node-richer ref="node_richer"></node-richer>
    </div>
    <div>
      <kg-json
        ref="kg_json"
        :data="graphData"
      ></kg-json>
    </div>
    <div>
      <kg-help ref="kg_help"></kg-help>
    </div>
    <div>
      <kg-wanted ref="kg_wanted"></kg-wanted>
    </div>
  </div>
</template>
<script>
import _ from "lodash";
import * as d3 from "d3";
import { kgBuilderApi } from "@/api";
import MenuBlank from "@/views/kgbuilder/components/menu_blank";
import MenuLink from "@/views/kgbuilder/components/menu_link";
import KgForm from "@/views/kgbuilder/components/kg_form";
import NodeRicher from "@/views/kgbuilder/components/node_richer";
import KgFocus from "@/components/KGFocus";
import KgWanted from "@/components/KGWanted";
import KgJson from "@/views/kgbuilder/components/kg_json";
import KgHelp from "@/views/kgbuilder/components/kg_help";
import html2canvas from "html2canvas";
import kgbuilder2 from '@/components/KGBuilder2'
import { EventBus } from '@/utils/event-bus.js'
import axios from 'axios'
export default {
  name: "kgBuilderv1",
  components: {
    MenuBlank,
    MenuLink,
    KgForm,
    NodeRicher,
    KgFocus,
    KgJson,
    KgHelp,
    KgWanted,
    kgbuilder2
  },
  provide() {
    return {
      _thisKey: this._thisKey,
      Dset: this.Dset,
      clickNode: this.clickNode,
      updateLinkName: this.updateLinkName,
      typeLinkName: this.typeLinkName,
      editLinkName: this.editLinkName,
      deleteLinkName: this.deleteLinkName
    }
  },
  data () {
    return {
      style: null,
      width: null,
      height: null,
      // 启用D3画图配置方法
      MethodsProperties: [
        { name: 'SingleClick', state: true }, // 单击
        { name: 'DoubleClick', state: true }, // 双击
        { name: 'NodeDrag', state: true }, // 节点拖动
        { name: 'LineDrag', state: true }, // 连线拖动
        { name: 'FollowDrag', state: true } // 节点子随父拖动而动
      ],
      // 连线颜色
      LinkColor: [
        { name: '具有', color: '#4A5150', id: 'arrow' },
        { name: '其它', color: '#16ACFF', id: 'arrowA' },
        { name: '学习', color: '#FC4040', id: 'arrowB' },
        { name: '包含', color: '#FBB613', id: 'arrowC' },
        { name: 'a11', color: '#202928', id: 'arrowD' }
      ],
      // 节点颜色
      NodeColor: [
        {
          name: '知识集',
          color1: '#FFA16E',
          color2: '#F48244',
          state: 'color'
        },
        {
          name: '知识块',
          color1: '#FB6E6F',
          color2: '#D13F3F',
          state: 'color'
        },
        {
          name: '知识点',
          color1: '#32E3C1',
          color2: '#21B598',
          state: 'color'
        },
        {
          name: '属性节点',
          color1: '#7E7E7E',
          color2: '#666666',
          state: 'color'
        },
        {
          name: '资源节点',
          color1: '#F48244',
          color2: '#F48244',
          state: 'color'
        }
      ],
      RingFunction: [
        {
          name: 'addNodeButtonsOne',
          data: 5, // 渲染4个
          datadefo: [
            // 新建
            {
              name: 'add',
              default: (d, _this, d3) => {
                const out_buttongroup_id = '.out_buttongroup_' + d.uuid
                _this.svg
                  .selectAll('.buttongroup')
                  .classed('circle_none', true)
                _this.svg
                  .selectAll(out_buttongroup_id)
                  .classed('circle_none', false)
                _this.ringFunction.filter((res) => {
                  if (res.name == 'addNodeButtonsTWO') {
                    for (let i = res.label.length - 1; i >= 0; i--) {
                      d3.selectAll('.' + res.id + i).style('display', 'block')
                    }
                  }
                })
              }
            },
            // 删除
            {
              name: 'delete',
              default: (d, _this, d3) => {
                let data = { domain: _this.domain, nodeId: d.uuid };
                  kgBuilderApi.deleteNode(data).then(result => {
                    if (result.code == 200) {
                      let rShips = result.data;
                      // 删除节点对应的关系
                      for (let m = 0; m < rShips.length; m++) {
                        for (let i = 0; i < _this.graphData.links.length; i++) {
                          if (_this.graphData.links[i].uuid == rShips[m].uuid) {
                            _this.graphData.links.splice(i, 1);
                            i = i - 1;
                          }
                        }
                      }
                      // 找到对应的节点索引
                      let j = -1;
                      for (let i = 0; i < _this.graphData.nodes.length; i++) {
                        if (_this.graphData.nodes[i].uuid == d.uuid) {
                          j = i;
                          break;
                        }
                      }
                      if (j >= 0) {
                        _this.graphData.nodes.splice(j, 1); // 根据索引删除该节点
                        _this.updateGraph();
                        _this.$message.success("操作成功!");
                      }
                    }
                  });
              }
            },
            // 展开
            {
              name: 'MORE',
              default: (d, _this, d3) => {
                let data = { domain: _this.domain, nodeId: d.uuid };
                kgBuilderApi.getMoreRelationNode(data).then(result => {
                  if (result.code == 200) {
                    //把不存在于画布的节点添加到画布
                    _this.mergeNodeAndLink(result.data.node, result.data.relationship);
                    //重新绘制
                    _this.updateGraph();
                  } else{
                    _this.$message.error('展开失败 :' + item.executionTime)
                  }
                });
              }
            },
            // 编辑
            {
              name: 'editor',
              default: (d, _this, d3) => {
                _this.$nextTick(() => {
                  let formNode = {
                    uuid: d.uuid,
                    name: d.name,
                    r: parseInt(d.r),
                    color: d.color
                  };
                 _this.$emit('editForm',true,"nodeEdit",formNode,_this.domainId);
                })
              }
            }
          ],
          label: [
            { name: '新建', state: 'text' },
            { name: '删除', state: 'text' },
            { name: '展开', state: 'text' },
            { name: '编辑', state: 'text' },
            { name: '连线', state: 'text' }

          ],
          id: 'action_',
          r: 25,
          default: function() {}
        },
        {
          name: 'addNodeButtonsTWO',
          data: 20, // 4*5 一层菜单是5个，2层菜单每个4个
          datadefo: [
            {
              name: 'spot',
              default: (d, _this, d3) => {
                  console.log("点击了点")
                  //_this.updateGraph() //刷新图谱
              }
            },
            {
              name: 'block',
              default: (d, _this, d3) => {
                 console.log("点击了块")
                  //_this.updateGraph() //刷新图谱
              }
            },
            {
              name: 'collection',
              default: (d, _this, d3) => {
                console.log("点击了集")
                  //_this.updateGraph() //刷新图谱
              }
            },
            {
              name: 'genus',
              default: (d, _this, d3) => {
                 console.log("点击了属")
                  //_this.updateGraph() //刷新图谱
              }
            }
          ],
          label: [
            { name: '点', state: 'text' },
            { name: '块', state: 'text' },
            { name: '集', state: 'text' },
            { name: '属', state: 'text' }
          ],
          id: 'actionA_',
          r: 75,
          default: function() {}
        },
        {
          name: 'addNodeButtonsOld',
          data: 2,
          datadefo: [
            {
              name: 'addLink',
              default: (d, _this, d3) => {
                const arr = []
                let name = ''
                _this.graphData.links.filter((lin) => {
                  if (lin.source == d.uuid || lin.target == d.uuid) {
                    if (
                      lin.source == _this.clone.id ||
                      lin.target == _this.clone.id
                    ) {
                      arr.push(lin.label)
                    }
                  }
                })
                this.$prompt('请输入连线名字', {
                  confirmButtonText: '确定',
                  cancelButtonText: '取消',
                  inputValidator: (res) => {
                    if (arr.includes(res)) {
                      return '连线不能重名'
                    } else if (arr.includes('包含') && res == null) {
                      return '不能重复新建默认连线名'
                    } else {
                      name = res
                    }
                  }
                })
                  .then(({ value }) => {
                    newNodeRalte({
                      relateLabel: name,
                      startNeo4jNodeId: d.uuid,
                      endNeo4jNodeId: _this.clone.id
                    }).then((res) => {
                      if (res.result == 'ok') {
                        // eslint-disable-next-line no-eval
                        res.data = eval('(' + res.data + ')')
                        const a = res.data.edges[0]
                        _this.graphData.links.push(a)
                        _this.updateGraph()
                        this.$message({
                          type: 'success',
                          message: '新建连线成功'
                        })
                      }
                    })
                  })
                  .catch(() => {
                    this.$message({
                      type: 'info',
                      message: '取消输入'
                    })
                  })
                // _this.graphData.nodes.filter((item) => {
                //   if (item.uuid == d.uuid) {
                //     console.log(item, _this.clone);
                //     newNodeRalte({
                //       startNeo4jNodeId: item.uuid,
                //       endNeo4jNodeId: _this.clone.id,
                //     }).then((res) => {
                //       if (res.result == "ok") {
                //         res.data = eval("(" + res.data + ")");
                //         let a = res.data.edges[0]
                //         _this.graphData.links.push(a)
                //         _this.updateGraph()
                //       }
                //     });
                //   }
                // });
              }
            },
            {
              name: 'clone',
              default: (d, _this, d3) => {
                _this.graphData.nodes.filter((item) => {
                  if (item.uuid == d.uuid) {
                    cloneNodePropertys({
                      startNeo4jNodeId: _this.clone.id,
                      endNeo4jNodeId: item.uuid
                    }).then((res) => {
                      if (res.result == 'ok') {
                        item.nodetype = _this.clone.nodetype
                        _this.updateGraph()
                      } else {
                        this.$message.error('克隆失败', res.message)
                      }
                    })
                  }
                })
              }
            }
          ],
          label: [
            {
              name: '新建关系',
              state: 'text'
            },
            {
              name: '克隆属性',
              state: 'text'
            }
          ],
          id: 'actionB_',
          r: 25,
          default: function() {}
        },
        {
          name: 'addNodeButtonsNEW',
          data: 3, // 渲染环形菜单数量
          datadefo: [
            {
              // 显示相关节点和连线
              name: 'SeeList',
              default: (d, _this, d3) => {
                sortView(_this.graphData.nodes, _this.graphData.links, d)
                function sortView(arr, link, d) {
                  const a = []
                  let id = ''
                  const ArrY = link.filter((item) => {
                    if (item.source === d.uuid || item.target === d.uuid) {
                      d3.selectAll('.Links_' + item.uuid).style(
                        'display',
                        'block'
                      )
                      d3.selectAll('.TextLink_' + item.uuid).style(
                        'display',
                        'block'
                      )
                      return item
                    }
                  })
                  ArrY.filter((item) => {
                    arr.filter((res) => {
                      if (item.source === res.uuid || item.target === res.uuid) {
                        const name = d3
                          .selectAll('.circle_' + res.uuid)
                          .style('display')
                        if (name === 'inline' || name === 'block') {
                          a.push(true)
                        } else {
                          d3.selectAll('.circle_' + res.uuid).style(
                            'display',
                            'block'
                          )
                          a.push(false)
                          id = res
                        }
                      }
                    })
                  })
                  a.some((i) => {
                    if (i == false) {
                      sortView(arr, link, id)
                    }
                  })
                }
              }
            }, // //标签文字
            {
              name: 'Share',
              default: (d, _this, d3) => {
                this.Nodellabelist = d
                this.Visiblelabel = true
                this.$nextTick(() => {
                  this.$refs.nodeTagSearchlabel.getNodeDefault(d)
                })
              }
            },
            // 隐藏相关节点和连线
            {
              name: 'SeeName',
              default: (d, _this, d3) => {
                _this.graphData.links.filter((item, i) => {
                  if (item.source === d.uuid || item.target === d.uuid) {
                    d3.selectAll('.Links_' + item.uuid).style('display', 'none')
                    d3.selectAll('.TextLink_' + item.uuid).style(
                      'display',
                      'none'
                    )
                  }
                })
                _this.graphData.nodes.filter((res, i) => {
                  if (res.uuid === d.uuid) {
                    d3.selectAll('.circle_' + res.uuid).style('display', 'none')
                  }
                })
              }
            }
          ],
          label: [
            {
              name: 'https://img-s-msn-com.akamaized.net/tenant/amp/entityid/BB12cBeO.img',
              state: 'url'
            },
            {
              name: (d) => {
                return d.name
              },
              state: 'Dtext'
            },
            {
              name: 'https://img-s-msn-com.akamaized.net/tenant/amp/entityid/BB12cTXD.img',
              state: 'url'
            }
          ],
          id: 'actionC_',
          r: 25,
          default: function() {}
        }
      ],
      // eslint-disable-next-line vue/no-reserved-keys
      _thisView: null,
      timer: null,
      tooltip: null,
      nodeDetail: null,
      pageSizeList: [
        { size: 500, isActive: true },
        { size: 1000, isActive: false },
        { size: 2000, isActive: false },
        { size: 5000, isActive: false }
      ],
      domain: "",
      domainId: 0,
      nodeName: "",
      pageSize: 500,
      activeNode: null,
      nodeImageList: [],
      showImageList: [],
      editorContent: "",
      pageModel: {
        pageIndex: 1,
        pageSize: 30,
        totalCount: 0,
        totalPage: 0,
        nodeList: []
      },
      graphData: {
        nodes: [],
        links: []
      },
      jsonShow: false,
      helpShow: false
    };
  },
  filters: {
    labelFormat: function (value) {
      let domain = value.substring(1, value.length - 1);
      return domain;
    }
  },
  mounted () {

  },
  created () {
    this.getDomain();
    this.$nextTick(() => {
      this.width = document.getElementsByClassName('graphContainer')[0].offsetWidth
      //this.height = document.getElementsByClassName('graphContainer')[0].offsetHeight
      this.height = window.screen.height
      this.style = {
        width: this.width + 'px',
        height: this.height + 'px'
      }
      //console.log(this.width, this.height)
      EventBus.$emit('DIV', this.width, this.height)
    })
  },
  methods: {
    _thisKey(item) {
      this._thisView = item
    },
    Dset(item) {
      this.d3 = item
    },
    clickNode(d){
      console.log(d);
    },
    prev () {
      if (this.pageModel.pageIndex > 1) {
        this.pageModel.pageIndex--;
        this.getDomain();
      }
    },
    next () {
      if (this.pageModel.pageIndex < this.pageModel.totalPage) {
        this.pageModel.pageIndex++;
        this.getDomain();
      }
    },
    editForm(flag,action,data,domainId){
      this.$refs.kg_form.initNode(flag,action,data,domainId);
    },
    //创建节点
    createNode (graphNode) {
      let data = graphNode;
      data.domain = this.domain;
      let _this = this;
      kgBuilderApi.createNode(data).then(result => {
        if (result.code == 200) {
          //删除旧节点，由于我们改变的是属性，不是uuid,此处我们需要更新属性，或者删除节点重新添加
          let newNode = result.data;
          for (let i = 0; i < _this.graphData.nodes.length; i++) {
            if (_this.graphData.nodes[i].uuid == graphNode.uuid) {
              _this.graphData.nodes.splice(i, 1);
            }
          }
          _this.graphData.nodes.push(newNode);

          //_this.$refs.kg_builder.updateGraph();
        }
      });
    },
    //画布直接添加节点
    createSingleNode (left, top) {
      let data = { name: "", r: 30 };
      data.domain = this.domain;
      kgBuilderApi.createNode(data).then(result => {
        if (result.code == 200) {
          let newNode = result.data;
          _.assignIn(newNode, {
            x: left,
            y: top,
            fx: left,
            fy: top,
            r: parseInt(newNode.r)
          });
          this.graphData.nodes.push(newNode);
          this.updateGraph();
        }
      });
    },
    //删除节点
    deleteNode (out_buttongroup_id) {
      let _this = this;
      _this
        .$confirm(
          "此操作将删除该节点及周边关系(不可恢复), 是否继续?",
          "三思而后行",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
          }
        )
        .then(function () {
          let data = { domain: _this.domain, nodeId: _this.selectNode.nodeId };
          kgBuilderApi.deleteNode(data).then(result => {
            if (result.code == 200) {
              _this.svg.selectAll(out_buttongroup_id).remove();
              let rShips = result.data;
              // 删除节点对应的关系
              for (let m = 0; m < rShips.length; m++) {
                for (let i = 0; i < _this.graphData.links.length; i++) {
                  if (_this.graphData.links[i].uuid == rShips[m].uuid) {
                    _this.graphData.links.splice(i, 1);
                    i = i - 1;
                  }
                }
              }
              // 找到对应的节点索引
              let j = -1;
              for (let i = 0; i < _this.graphData.nodes.length; i++) {
                if (_this.graphData.nodes[i].uuid == _this.selectNode.nodeId) {
                  j = i;
                  break;
                }
              }
              if (j >= 0) {
                _this.selectNode.nodeId = 0;
                _this.graphData.nodes.splice(j, 1); // 根据索引删除该节点
                _this.updateGraph();
                _this.$message({
                  type: "success",
                  message: "操作成功!"
                });
              }
            }
          });
        })
        .catch(function () {
          _this.$message({
            type: "info",
            message: "已取消删除"
          });
        });
    },
    //删除连线
    deleteLink () {
      let _this = this;
      _this
        .$confirm("此操作将删除该关系(不可恢复), 是否继续?", "三思而后行", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        })
        .then(function () {
          let data = { domain: _this.domain, shipId: _this.selectNode.nodeId };
          kgBuilderApi.deleteLink(data).then(result => {
            if (result.code == 200) {
              let j = -1;
              for (let i = 0; i < _this.graphData.links.length; i++) {
                if (_this.graphData.links[i].uuid == _this.selectNode.nodeId) {
                  j = i;
                  break;
                }
              }
              if (j >= 0) {
                _this.selectNode.nodeId = 0;
                _this.graphData.links.splice(j, 1);
                _this.updateGraph();
                _this.isDeleteLink = false;
              }
            }
          });
        })
        .catch(function () {
          _this.$message({
            type: "info",
            message: "已取消删除"
          });
        });
    },
    //添加连线
    createLink (sourceId, targetId, ship) {
      let data = {
        domain: this.domain,
        sourceId: sourceId,
        targetId: targetId,
        ship: ship
      };
      kgBuilderApi.createLink(data).then(result => {
        if (result.code == 200) {
          let newShip = result.data;
          this.graphData.links.push(newShip);
          this.updateGraph();
          this.isAddLink = false;
        }
      });
    },
    //更新连线名称
    updateLinkName () {
      let _this = this;
      this.$prompt("请输入关系名称", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        inputValue: this.selectlinkname
      })
        .then(function (res) {
          let value = res.value;
          let data = {
            domain: _this.domain,
            shipId: _this.selectNode.nodeId,
            shipName: value
          };
          kgBuilderApi.updateLink(data).then(result => {
            if (result.code == 200) {
              let newShip = result.data;
              _this.graphData.links.forEach(function (m) {
                if (m.uuid == newShip.uuid) {
                  m.name = newShip.name;
                }
              });
              _this.selectNode.nodeId = 0;
              _this.updateGraph();
              _this.isAddLink = false;
              _this.selectlinkname = "";
            }
          });
        })
        .catch(function () {
          _this.$message({
            type: "info",
            message: "取消输入"
          });
        });
    },
    //更新节点名称
    updateNodeName (d) {
      let _this = this;
      _this
        .$prompt("编辑节点名称", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          inputValue: d.name
        })
        .then(function (res) {
          let value = res.value;
          let data = { domain: _this.domain, nodeId: d.uuid, nodeName: value };
          kgBuilderApi.updateNodeName(data).then(result => {
            if (result.code == 200) {
              if (d.uuid != 0) {
                for (let i = 0; i < _this.graphData.nodes.length; i++) {
                  if (_this.graphData.nodes[i].uuid == d.uuid) {
                    _this.graphData.nodes[i].name = value;
                  }
                }
              }
              _this.updateGraph();
              _this.$message({
                message: "操作成功",
                type: "success"
              });
            }
          });
        })
        .catch(function () {
          _this.$message({
            type: "info",
            message: "取消操作"
          });
        });
    },
    //初始化节点富文本内容
    initNodeContent () {
      let data = { domainId: this.domainId, nodeId: this.selectNode.nodeId };
      kgBuilderApi.getNodeContent(data).then(response => {
        if (response.code == 200) {
          if (response.data) {
            this.$refs.kg_form.initContent(response.data.content);
          } else {
            this.$message.warning("暂时没有更多数据");
          }
        }
      });
    },
    //初始化节点添加的图片
    initNodeImage () {
      let data = { domainId: this.domainId, nodeId: this.selectNode.nodeId };
      kgBuilderApi.getNodeImage(data).then(response => {
        if (response.code == 200) {
          if (response.data) {
            let nodeImageList = [];
            for (let i = 0; i < response.data.length; i++) {
              nodeImageList.push({
                file: response.data[i].fileName,
                imageType: response.data[i].imageType
              });
              this.$refs.kg_form.initImage(nodeImageList);
            }
          } else {
            this.$message.warning("暂时没有更多数据");
          }
        }
      });
    },
    //一次性获取富文本和图片
    getNodeDetail (nodeId, left, top) {
      let data = { domainId: this.domainId, nodeId: nodeId };
      kgBuilderApi.getNodeDetail(data).then(result => {
        if (result.code == 200) {
          if (result.data) {
            this.$refs.node_richer.init(
              result.data.content,
              result.data.imageList,
              left,
              top
            );
          } else {
            this.$message.warning("暂时没有更多数据");
          }
        }
      });
    },
    //全屏
    requestFullScreen () {
      let element = document.getElementById("graphcontainerdiv");
      let width = window.screen.width;
      let height = window.screen.height;
      this.svg.attr("width", width);
      this.svg.attr("height", height);
      if (element.requestFullscreen) {
        element.requestFullscreen();
      }
      // FireFox
      else if (element.mozRequestFullScreen) {
        element.mozRequestFullScreen();
      }
      // Chrome等
      else if (element.webkitRequestFullScreen) {
        element.webkitRequestFullScreen();
      }
      // IE11
      else if (element.msRequestFullscreen) {
        element.msRequestFullscreen();
      }
    },
    //获取图谱节点及关系
    getDomainGraph () {
      //this.loading = true;
      let data = {
        domain: this.domain,
        nodeName: this.nodeName,
        pageSize: this.pageSize
      };
      let _this=this;
      // axios.get('/static/kgData.json', {}).then(function (response) {
      //   var data = response.data
      //   console.log(data)
      //   _this.graphData=data;
      // //_this.graphData.nodes = data.node;
      //     // _this.graphData.links =data.relationship;
      // })
      // d3.select(".graphContainer >svg").remove();
      kgBuilderApi.getDomainGraph(data).then(result => {
        if (result.code == 200) {
          if (result.data != null){
              _this.graphData={nodes: [],links: []};
             _this.graphData.nodes = result.data.node;
             _this.graphData.links = result.data.relationship;
          }
        }
      });
    },
    //展开更多节点
    getMoreNode () {
      let data = { domain: this.domain, nodeId: this.selectNode.nodeId };
      kgBuilderApi.getMoreRelationNode(data).then(result => {
        if (result.code == 200) {
          //把不存在于画布的节点添加到画布
          this.mergeNodeAndLink(result.data.node, result.data.relationship);
          //重新绘制
          this.updateGraph();
        }
      });
    },
    //快速添加
    btnQuickAddNode () {
      this.$refs.kg_form.init(true, "batchAdd");
    },
    //删除领域
    deleteDomain (id, value) {
      this.$confirm(
        "此操作将删除该标签及其下节点和关系(不可恢复), 是否继续?",
        "三思而后行",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      )
        .then(function (res) {
          let data = { domainId: id, domain: value };
          kgBuilderApi.deleteDomain(data).then(result => {
            if (result.code == 200) {
              this.getDomain();
              this.domain = "";
            }
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除"
          });
        });
    },
    //创建新领域
    createDomain (value) {
      this.$prompt("请输入领域名称", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消"
      })
        .then(res => {
          value = res.value;
          let data = { domain: value, type: 0 };
          kgBuilderApi.createDomain(data).then(result => {
            if (result.code == 200) {
              this.getDomain();
              this.domain = value;
              this.getDomainGraph();
            }
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "取消输入"
          });
        });
    },
    //添加单个节点，改变鼠标样式为+
    btnAddSingle () {
      d3.select(".graphContainer").style("cursor", "crosshair"); //进入新增模式，鼠标变成＋
    },
    //获取领域标签
    getLabels (data) {
      kgBuilderApi.getDomains(data).then(result => {
        if (result.code == 200) {
          this.pageModel = result.data;
          this.pageModel.totalPage =
            parseInt((result.data.totalCount - 1) / result.data.pageSize) + 1;
        }
      });
    },
    getDomain (pageIndex) {
      this.pageModel.pageIndex = pageIndex
        ? pageIndex
        : this.pageModel.pageIndex;
      let data = {
        pageIndex: this.pageModel.pageIndex,
        pageSize: this.pageModel.pageSize,
        command: 0
      };
      this.getLabels(data);
    },
    matchDomainGraph (domain) {
      this.domain = domain.name;
      this.domainId = domain.id;
      this.getDomainGraph();
    },
    //保存图片
    saveImage () {
      html2canvas(document.querySelector(".graphContainer"), {
        width: document.querySelector(".graphContainer").offsetWidth, // canvas画板的宽度 一般都是要保存的那个dom的宽度
        height: document.querySelector(".graphContainer").offsetHeight, // canvas画板的高度  同上
        scale: 1
      }).then(function (canvas) {
        let a = document.createElement("a");
        a.href = canvas.toDataURL("image/png"); //将画布内的信息导出为png图片数据
        let timeStamp = Date.parse(new Date());
        a.download = timeStamp; //设定下载名称
        a.click(); //点击触发下载
      });
    },
    showJsonData () {
      this.$refs.kg_json.init();
    },
    wanted () {
      this.$refs.kg_wanted.init();
    },
    //导入图谱
    importGraph () {
      this.$refs.kg_form.init(true, "import");
    },
    exportGraph () {
      if (!this.domain || this.domain == '') {
        this.$message.warning("请选择一个领域");
        return;
      }
      let data = { domain: this.domain };
      kgBuilderApi.exportGraph(data).then(result => {
        if (result.code == 200) {
          window.location.href = result.fileName
        }
      });
    },
    help () {
      this.$refs.kg_help.init();
    },
    //设置画布内最大的点个数
    setMatchSize (m) {
      for (let i = 0; i < this.pageSizeList.length; i++) {
        this.pageSizeList[i].isActive = false;
        if (this.pageSizeList[i].size == m.size) {
          this.pageSizeList[i].isActive = true;
        }
      }
      this.pageSize = m.size;
      this.getDomainGraph();
    },
    //合并节点和连线
    mergeNodeAndLink (newNodes, newLinks) {
      let _this = this;
      newNodes.forEach(function (m) {
        let sobj = _this.graphData.nodes.find(function (x) {
          return x.uuid === m.uuid;
        });
        if (typeof sobj == "undefined") {
          _this.graphData.nodes.push(m);
        }
      });
      newLinks.forEach(function (m) {
        let sobj = _this.graphData.links.find(function (x) {
          return x.uuid === m.uuid;
        });
        if (typeof sobj == "undefined") {
          _this.graphData.links.push(m);
        }
      });
    },
    //批量添加节点
    batchCreateNode (param) {
      let data = {
        domain: this.domain,
        sourceName: param.sourceNodeName,
        targetNames: param.targetNodeNames,
        relation: param.relation
      };
      kgBuilderApi.batchCreateNode(data).then(result => {
        if (result.code == 200) {
          //把不存在于画布的节点添加到画布
          this.mergeNodeAndLink(result.data.nodes, result.data.ships);
          //重新绘制
          this.updateGraph();
          this.$message({
            message: "操作成功",
            type: "success"
          });
        }
      });
    },
    //批量添加子节点
    batchCreateChildNode (param) {
      let data = {
        domain: this.domain,
        sourceId: this.selectNode.nodeId,
        targetNames: param.targetNodeNames,
        relation: param.relation
      };
      kgBuilderApi.batchCreateChildNode(data).then(result => {
        if (result.code == 200) {
          //把不存在于画布的节点添加到画布
          this.mergeNodeAndLink(result.data.nodes, result.data.ships);
          //重新绘制
          this.updateGraph();
          this.$message({
            message: "操作成功",
            type: "success"
          });
        }
      });
    },
    //批量添加同级节点
    batchCreateSameNode (param) {
      let data = {
        domain: this.domain,
        sourceNames: param.sourceNodeName
      };
      kgBuilderApi.batchCreateSameNode(data).then(result => {
        if (result.code == 200) {
          //把不存在于画布的节点添加到画布
          this.mergeNodeAndLink(result.data, null);
          //重新绘制
          this.updateGraph();
          this.$message({
            message: "操作成功",
            type: "success"
          });
        }
      });
    },
    //画布右击
    initContainerRightClick (event) {
      let _this = this;
      _this.mouserPos = {
        left: event.clientX,
        top: event.clientY
      };
      let menuBar = {
        left: event.clientX,
        top: event.clientY,
        show: true
      };
      _this.$refs.menu_blank.init(menuBar);
      event.preventDefault();
    },
    //画布点击
    initContainerLeftClick (event) {
      let _this = this;
      _this.mouserPos = {
        left: event.clientX,
        top: event.clientY
      };
      _this.$refs.menu_blank.init({ show: false });
      _this.$refs.menu_link.init({ show: false });
      _this.$refs.node_richer.close();
      if (event.target.tagName != "circle" && event.target.tagName != "link") {
        d3.select("#nodeDetail").style("display", "none");
      }
      let cursor = document.getElementById("graphContainer").style.cursor;
      if (cursor == "crosshair") {
        d3.select(".graphContainer").style("cursor", "");
        _this.createSingleNode(event.offsetX, event.offsetY);
      }
      event.preventDefault();
    }
  }
};
</script>
<style>
.graphContainer{
  height: 100vh -50px;
}
.mind-box {
  height: calc(100vh - 85px);
  overflow: hidden;
}
.mind-l {
  width: 300px;
  float: left;
  background: #f7f9fc;
  height: 100%;
  border-right: 1px solid #d3e2ec;
}
.ml-ht {
  padding-top: 20px;
  line-height: 50px;
  font-size: 16px;
  font-weight: 400;
  text-align: center;
  color: #333;
  border-bottom: 1px solid #d3e2ec;
}
.ml-a-box {
  margin: 10px;
}
.ml-a {
  display: inline-block;
  min-width: 46px;
  line-height: 1;
  padding: 6px 8px 6px 8px;
  margin: 0 4px 5px 0;
  background: #fff;
  border: 1px solid #e3e3e3;
  box-sizing: border-box;
  transition: 0.3s;
}
.ml-a span {
  max-width: 190px;
  display: inline-block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  vertical-align: middle;
}
.ml-a-all {
  display: block;
  margin: 10px 10px 0;
  text-align: center;
}
.ml-a span:empty:before {
  content: '閺堫亜鎳￠崥锟�';
  color: #adadad;
}
.ml-a small {
  color: #999;
}
.ml-a:hover {
  background: #f4f4f4;
}
.ml-a.cur,
.ml-a.cur small {
  background: #156498;
  color: #fff;
}
.ml-btn-box {
  text-align: right;
  padding: 0 10px;
  margin-bottom: 20px;
}
.ml-btn {
  padding: 0 5px;
  color: #156498;
}
.mind-con {
  height: calc(100vh - 40px);
  overflow: hidden;
  background: #fff;
  display: -webkit-flex;
  display: flex;
  flex-direction: column;
  padding: 5px;
}
.mind-top {
  /* line-height: 70px;
  height: 70px; */
  padding: 0 22px;
  border-bottom: 1px solid #ededed;
}
.mt-m {
  color: #666;
  margin-right: 30px;
}
.mt-m i {
  font-size: 18px;
  color: #333;
  font-weight: 700;
  font-style: normal;
}
.mb-con .search,
.mind-top .search {
  border: 1px solid #e2e2e2;
}
.svg-a-sm {
  font-size: 14px;
  color: #156498;
  margin-right: 30px;
  cursor: pointer;
}
.mind-cen {
  height: calc(100% - 70px);
}
.half-auto {
  height: 40%;
}
.mind-bottom {
  height: 490px;
  box-sizing: border-box;
  border-top: 1px solid #ededed;
}
.ss-d {
  display: inline-block;
  vertical-align: middle;
  margin-right: 10px;
  border-radius: 50%;
  background: #dedede;
}
.sd {
  margin: 2px;
}
.sd-active {
  color: red !important;
  background: none !important;
}
.btn-line + .btn-line {
  margin-left: 10px;
}
.co {
  color: #ee8407 !important;
}
a {
  text-decoration: none;
}
.a {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.fl {
  float: left;
}
.fr {
  float: right;
  margin: 7px;
}
.tl {
  text-align: left;
}
.pl-20 {
  padding-left: 20px;
}
text {
  cursor: pointer;
  max-width: 25px;
  display: inline-block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  vertical-align: middle;
}
circle {
  cursor: pointer;
}
#graphcontainerdiv {
  background: #fff;
}
.el-color-picker__panel {
  left: 812px !important;
}
.wange-toolbar {
  border: 1px solid #ccc;
}
.wangeditor-form {
  border: 1px solid #ccc;
  height: 350px;
  min-height: 340px;
}
.el-tag {
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.mind-fj-box {
  display: inline-block;
  width: 290px;
  padding: 5px;
  border: 1px solid #e6e6e6;
  box-shadow: 0 0 8px rgba(206, 205, 201, 0.38);
}
.mind-fj-p {
  color: #666;
  line-height: 24px;
  padding: 5px;
  background: rgba(255, 255, 255, 0.85);
}
.mind-carousel + .mind-fj-p .el-scrollbar__wrap {
  height: auto;
  max-height: 220px;
  min-height: 0;
}
.carous-img {
  height: 100%;
  background: rgba(0, 0, 0, 0.1);
  line-height: 197px;
  text-align: center;
}
.carous-img img {
  max-width: 100%;
  max-height: 100%;
  line-height: 197px;
  vertical-align: middle;
}

.node_detail {
  position: absolute;
  width: 100%;
  line-height: 35px;
  -webkit-border-radius: 10px;
  -moz-border-radius: 10px;
  border-radius: 10px;
  font-size: 12px;
  padding-bottom: 10px;
  background: rgba(198, 226, 255, 0.2);
  display: none;
}
.node_pd {
  padding: 4px;
  font-size: 13px;
  font-family: -webkit-body;
  font-weight: 600;
}
.operatetips {
  position: absolute;
  right: 10px;
  float: right;
  top: 0;
  width: 335px;
  padding: 30px;
  border: 2px #ee7942 solid;
  border-radius: 4px;
}
.jsoncontainer {
  position: absolute;
  right: 30%;
  float: right;
  top: 0;
  width: 60%;
  height: 60%;
  padding: 30px;
  border: 2px #ee7942 solid;
  border-radius: 4px;
  background: #fff;
}
.cypher_toolbar {
  line-height: 70px;
  height: 85px;
  padding: 0 22px;
  border-bottom: 1px solid #ededed;
}
.hometitle {
  font-size: 18px;
  color: #282828;
  font-weight: 600;
  margin: 0;
  text-transform: uppercase;
  padding-bottom: 15px;
  margin-bottom: 25px;
  position: relative;
}

.el-scrollbar {
  overflow: hidden;
  position: relative;
}
ul {
  padding: 0px;
}
.icon {
  width: 1em;
  height: 1em;
  vertical-align: -0.15em;
  fill: currentColor;
  overflow: hidden;
}
.el-button {
  display: inline-block;
  line-height: 1;
  white-space: nowrap;
  cursor: pointer;
  background: #fff;
  border: 1px solid #d8dce5;
  color: #5a5e66;
  -webkit-appearance: none;
  text-align: center;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  outline: 0;
  margin: 0;
  -webkit-transition: 0.1s;
  transition: 0.1s;
  font-weight: 500;
  padding: 12px 20px;
  font-size: 14px;
  border-radius: 4px;
}
.search {
  position: relative;
  width: 220px;
  height: 32px;
  border-radius: 32px;
  overflow: hidden;
}
.search .el-input__inner {
  box-sizing: border-box;
  padding-left: 15px;
  height: 32px;
  line-height: 32px;
  padding-right: 40px;
  background: transparent;
  border-radius: 32px;
  border: none;
  transition: background 0.3s;
}
.search .el-button--default {
  position: absolute;
  right: 1px;
  float: right;
  padding: 0 10px;
  font-size: 22px;
  line-height: 29px;
  color: #7c9cb2;
  background: transparent;
  border: none;
  z-index: 1;
}
.search .el-button--default:hover {
  color: #156498;
  background: transparent;
  border: none;
}
.top .search {
  margin-left: 30px;
  background: rgba(0, 0, 0, 0.25);
  display: none;
}
.circle_none {
  display: none;
}
.dibmr {
  padding: 4px;
  display: inline-block;
  line-height: 30px;
}
</style>
