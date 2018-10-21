 var svg;
	  var force;
	  var index = 0;
	  var drag;
	  var width = 1140, height = 600;
	  var color = ["#FF8000", "#9393FF", "#0080FF","#FF83FA","#FFFF00"];
	  var getdomaingraph = function () { 
		   var _this=this;
	       _this.loading = true;
	       var data = {
	       	domain:_this.domain
	       }
	       $.ajax({
	           data: data,
	           type: "POST",
	           url: contextRoot+"kg/getdomaingraph",
	           success: function (result) {
	               if (result.code == 200) {
	            	   var graphModel=result.data;
	            	   debugger
	            	   if(graphModel!=null){
	            		   _this.graph.nodes=graphModel.node;
		            	   _this.graph.links=graphModel.relationship;
		            	   _this.loadChart();
	            	   }
	               } 
	           }
	       }); 
	   };
	   
	   var getcurrentnodeinfo = function (node) { 
		   var _this=this;
		   var data ={domain:_this.domain,nodeid:node.uuid};
	       //debugger
	       $.ajax({
	           data: data,
	           type: "POST",
	           url: contextRoot+"kg/getrelationnodecount",
	           success: function (result) {
	               if (result.code == 200) {
	            	   _this.selectnode.name=node.name;
	            	   _this.selectnode.count=result.data;
	               } 
	           }
	       }); 
	   };
	   var createnode = function () { 
		   var _this=this;
	       var data =_this.graphEntity;
	       data.domain=_this.domain;
	       //data.filter=_this.graphEntity.filter.split(";")
	       debugger
	       $.ajax({
	           data: data,
	           type: "POST",
	           traditional:true,
	           url: contextRoot+"kg/createnode",
	           success: function (result) {
	               if (result.code == 200) {
	            	   if(_this.graphEntity.uuid!=0){
	            		   for (var i = 0; i < _this.graph.nodes.length; i++) {
								if(_this.graph.nodes[i].uuid==_this.graphEntity.uuid){
									_this.graph.nodes.splice(i,1);
			            		}
	            	   }}
	            	  _this.graph.nodes.push(result.data);
	            	   d3.select(".graphcontainer").selectAll('*').remove();
	            	   _this.resetentity();
	            	   _this.getdomainnodes();
	            	   _this.loadChart();
	            	   _this.isedit=false;
	            	  _this.resetsubmit();
	               } 
	           }
	       }); 
	   };
	   var getdomainnodes = function (type) { 
		   //debugger;
		   var _this=this;
	       var data ={domain:_this.domain,entitytype:type};
	       $.ajax({
	           data: data,
	           type: "POST",
	           url: contextRoot+"kg/getdomainnodes",
	           success: function (result) {
	               if (result.code == 200) {
	            	   var item=result.data;
	            	   //debugger;
	            	   _this.domainnodes=item.concepts;
	            	   _this.domainpropnodes=item.props;
	            	   _this.domainmethodnodes=item.methods;
	            	   _this.domainentitynodes=item.entitys;
	               } 
	           }
	       }); 
	   };
	   var deletenode = function () { 
		   //debugger;
		   var _this=this;
	       var data ={domain:_this.domain,nodeid:_this.selectnodeid};
	       $.ajax({
	           data: data,
	           type: "POST",
	           url: contextRoot+"kg/deletenode",
	           success: function (result) {
	               if (result.code == 200) {
	            	   var rships=result.data;
						for (var m = 0; m < rships.length; m++) {
							 for (var i = 0; i < _this.graph.links.length; i++) {
								if(_this.graph.links[i].uuid==rships[m].uuid){
									_this.graph.links.splice(i,1);
									i=i-1;
			            		}
						}
					  }
	            	   var j=-1;
	            	   for (var i = 0; i < _this.graph.nodes.length; i++) {
							if(_this.graph.nodes[i].uuid==_this.selectnodeid){
	            			   j=i;
	            			   break ;
	            		   }
						}
	            	   if(j>=0){
	            		   _this.selectnodeid=0;
		            	   _this.graph.nodes.splice(i,1);
		            	   d3.select(".graphcontainer").selectAll('*').remove();
		            	   _this.loadChart();
		            	   _this.resetentity();
		            	   _this.getdomainnodes();
		            	   _this.$message({
					            type: 'success',
					            message: '操作成功!'
					          });
	            	   }
	            	  
	               } 
	           }
	       }); 
	   };
	   var createlink = function (sourceId,targetId,ship) { 
		   var _this=this;
	       var data ={domain:_this.domain,sourceid:sourceId,targetid:targetId,ship:''};
	       //debugger
	       $.ajax({
	           data: data,
	           type: "POST",
	           url: contextRoot+"kg/createlink",
	           success: function (result) {
	               if (result.code == 200) {
	            	   //debugger;
	            	   var newship=result.data;
	            	   _this.graph.links.push(newship); 
	            	   d3.select(".graphcontainer").selectAll('*').remove();
	            	   _this.loadChart();
	            	   _this.isaddlink=false;
	            	   _this.tips="";
	            	  
	               } 
	           }
	       }); 
	   };
	   var deletelink = function () { 
		   var _this=this;
	       var data ={domain:_this.domain,shipid:_this.updateshipid};
	       //debugger
	       $.ajax({
	           data: data,
	           type: "POST",
	           url: contextRoot+"kg/deletelink",
	           success: function (result) {
	               if (result.code == 200) {
	            	   var j=-1;
	            	   for (var i = 0; i < _this.graph.links.length; i++) {
							if(_this.graph.links[i].uuid==_this.updateshipid){
	            			   j=i;
	            			   break ;
	            		   }
						}
	            	   if(j>=0){
	            		   _this.updateshipid=0;
		            	   _this.graph.links.splice(i,1);
		            	   d3.select(".graphcontainer").selectAll('*').remove();
		            	   _this.loadChart();
		            	   _this.getdomainnodes();
		            	   _this.isdeletelink=false;
		            	   _this.tips="";
	            	   }
	               } 
	           }
	       }); 
	   };
	   var updatelinkName = function (shipName) { 
		   var _this=this;
	       var data ={domain:_this.domain,shipid:_this.updateshipid,shipname:shipName};
	       $.ajax({
	           data: data,
	           type: "POST",
	           url: contextRoot+"kg/updatelink",
	           success: function (result) {
	               if (result.code == 200) {
	            	   //debugger;
	            	   var newship=result.data;
	            	   _this.graph.links.forEach(function (m) {
	            		   if(m.uuid==newship.uuid){
	            			   //debugger;
	            			   m.name=newship.name;
	            		   }
	            	   });
	            	   _this.updateshipid=0;
	            	   d3.select(".graphcontainer").selectAll('*').remove();
	            	   _this.loadChart();
	            	   _this.getdomainnodes();
	            	   _this.isaddlink=false;
	            	  
	               } 
	           }
	       }); 
	   };
	   var addmaker=function(){
		   var arrowMarker = svg.append("marker")
	        .attr("id","arrow")
	        .attr("markerUnits","strokeWidth")
	        .attr("markerWidth","18")//
	        .attr("markerHeight","18")
	        .attr("viewBox","0 0 12 12")
	        .attr("refX","30")//13
	        .attr("refY","6")
	        .attr("orient","auto");
			var arrow_path = "M2,2 L10,6 L2,10 L6,6 L2,2";//定义箭头形状
			arrowMarker.append("path").attr("d",arrow_path).attr("fill","#383838");
	   }
	   var addmaker2=function(){
		   var arrowMarker = svg.append("marker")
	        .attr("id","arrow2")
	        .attr("markerUnits","strokeWidth")
	        .attr("markerWidth","6")//
	        .attr("markerHeight","6")
	        .attr("viewBox","0 0 12 12")
	        .attr("refX","19")//13
	        .attr("refY","6")
	        .attr("orient","auto");
			var arrow_path = "M2,2 L10,6 L2,10 L6,6 L2,2";//定义箭头形状
			arrowMarker.append("path").attr("d",arrow_path).attr("fill","#383838");
	   }
	   var drawnode=function(nodes){
		   var _this=this;
		   svg.selectAll(".node").remove();    
		   var node = svg.selectAll(".node")
           .data(nodes)
           .enter()
           .append("circle")
           .attr("r", 30)
           .style("fill", function (d) {
           	if(typeof(d.entitytype)=='undefined'){
           		return "#8DEEEE";
           	}
              return color[d.entitytype];
           })
           .style("stroke", function (d) {
           	if(typeof(d.entitytype)=='undefined'){
           		return "#8DEEEE";
           	}
              return color[d.entitytype];
           })
           //.style("stroke-width", "10") //圆外面的轮廓线
           .style("stroke-opacity", "0.6")//圆外面的轮廓线的透明度
           .call(drag)
           .on("contextmenu",contextmenu);
			node.on("dblclick",function(d){
				d.fixed = false;
				d3.event.preventDefault();
			});
			var dragLine;//线条
			var sourcenode=null,targetnode=null;
			node.on("click",function(d){
				var aa= d3.select(this)[0][0];
				 if (aa.classList.contains("selected")) {
					 d3.select(this).style("stroke-width", "2") //圆外面的轮廓线
					 d3.select(this).attr("class","")
				 }else{
					 d3.select(this).style("stroke-width", "20") 
					 d3.select(this).attr("class","selected")
				 }
				 _this.graphEntity=d;
				 //更新工具栏节点信息
				 _this.getcurrentnodeinfo(d);
				 
				 //添加连线状态
				 if(_this.isaddlink){
						if(sourcenode==null){
							d.fixed = true;
							sourcenode=d;
							dragLine=svg.append("line")//线条
							   .attr("class", "link")
							   .attr("stroke",color[d.entitytype])
							   .attr("stroke-width", 1)
							   .attr("marker-end","url(#arrow)");
							  /*  var position = d3.mouse(this);
							   dragLine
		                       .attr("x1", sourcenode.x)
		                       .attr("y1", sourcenode.y)
		                       .attr("x2", position[0])
		                       .attr("y2", position[1]); */
						}
						else{
							//debugger;
							if(sourcenode==d) return;
							targetnode=d;
							_this.createlink(sourcenode.uuid,targetnode.uuid,"RE")
							d.fixed = false
							sourcenode=null;
							targetnode=null;
							dragLine=null;
						}   
	                   d3.event.stopPropagation();
					 }
			});
			node.append("title")//为每个节点设置title（类似于html标签的title属性）
			.text(function (d) {
				return d.name;
			});
			
			return node;
	   }
	   var drawnodetext=function(nodes){
		   var _this=this;
		   var svg_texts = svg.selectAll(".nodetext")
			.data(nodes)
			.enter()
			.append("text")
			.style("fill", "black")
			.attr("class","nodetext")
			.attr("dy", 4)
			.attr("font-family", "微软雅黑")
            .attr("text-anchor", "middle")
			.text(function(d){
				return d.name;
			})
			.call(drag)
			.on("contextmenu",contextmenu);
			svg_texts.on("dblclick",function(d){
				d.fixed = false;
			});
			return svg_texts;
	   }
	   var drawlink=function(links){
		   var _this=this;
		 //加载连线
		   var linkItem = svg.selectAll("line").data(links);
		   var link=linkItem.enter().append("line")//线条
		   .attr("class", "link")
		   .attr("stroke", function (d) {
		       return color[d.source.entitytype];
		   })
		   .attr("stroke-width", 1)
		   .attr("marker-end","url(#arrow)");
		   link.on("dblclick",function(d){
			   _this.updateshipid=d.lk.uuid;
			   if(_this.isdeletelink){
				   _this.deletelink();
			   }else{
				   _this.showlinkprop();
			   }
			});
		   link.on("mouseenter",function(d){
			   d3.select(this).style("stroke-width", "6").attr("marker-end","url(#arrow2)");
			});
		   link.on("mouseleave",function(d){
			   d3.select(this).style("stroke-width", "1").attr("marker-end","url(#arrow)");
			});
		   return link;
	   }
	   var drawlinktext=function(links){
		   //debugger;
		   var linkItem = svg.selectAll(".linetext").data(links)
		   var linktext=linkItem.enter().append('text')
		   .attr("class","linetext")  
		   .style('fill', 'green')            
		   .style('font-size', '6px')            
		   //.style('font-weight', 'bold')    
		   .text(function(d){
			   //console.log(d);
			   if(d.lk.name!=''){
				   //return "RE["+d.lk.name+"]";
				   return d.lk.name;
			   }
				
			}) 
			 
			return linktext;
	   }
	   var forcetick=function(link,linktext,node,nodetext){
		   //debugger;
		   var _this=this;
		   force.on("tick", function () {
			 //更新连线坐标
				 link.attr("x1",function(d){ return d.source.x; })
				 		.attr("y1",function(d){ return d.source.y; })
				 		.attr("x2",function(d){ return d.target.x; })
				 		.attr("y2",function(d){ return d.target.y; })
				 		.attr("fill", "transparent");
			  //刷新连接线上的文字位置
			     linktext.attr("x",function(d){return (d.source.x+ d.target.x)/2;})
			            .attr("y",function(d){return (d.source.y + d.target.y)/2;});
				//更新节点坐标
				 node.attr("cx",function(d){ return d.x; })
				 		.attr("cy",function(d){ return d.y; });
				//更新文字坐标
				 nodetext.attr("x", function(d){ return d.x; })
				 	.attr("y", function(d){ return d.y; });   
	        });
	   }
	   var loadChart=function(){
		   var s=d3.select(".graphcontainer");
		   width=s[0][0].clientWidth;
		   
		   height=window.screen.height -154;//
		    svg = d3.select(".graphcontainer").append("svg")
           .attr("width", width)
           .attr("height", height)
           .attr('border', 'red')
           .style({
               'margin': '0 auto',
               'display': 'block'
           })
           .on("contextmenu",contextmenu);
   		   var nodes= this.graph.nodes, /*nodes() 里传入顶点的数组*/
   		   lks=this.graph.links,
           links = [];
		   lks.forEach(function (m) {
			    var sobj=nodes.find(function (x) {
				    return parseInt(x.uuid) === m.sourceid
				})
				if(typeof(sobj)=="undefined") return;
				var tobj=nodes.find(function (y) {
				    return parseInt(y.uuid) === m.targetid
				})
				if(typeof(tobj)=="undefined") return;
				var s=nodes.indexOf(sobj),
		            t = nodes.indexOf(tobj); // 中间节点
		       links.push({source: s, target: t,lk:m});
		   });
		    force = d3.layout.force();/*layout将json格式转化为力学图可用的格式*/
			force.nodes(nodes)
			.links(links)
			.size([width, height])
			.linkDistance(150)/*指定结点连接线的距离，默认为20*/
			.charge(-2000)/*顶点的电荷数。该参数决定是排斥还是吸引，数值越小越互相排斥*/
			.chargeDistance(2000)
			.start();
			 drag = force.drag()
			.on("dragstart",function(d,i){
				 d.fixed = true//拖拽開始后设定被拖拽对象为固定
				 d.fx = d.x;
				 d.fy = d.y;
				 d3.event.sourceEvent.stopPropagation();//缩放zoom时节点无法拖动，只能整体移动解决
			})
			//添加箭头
			this.addmaker();
			this.addmaker2();
			//添加连线
		    var link=this.drawlink(links);
			 //添加连线上的文字
		    var linktext=this.drawlinktext(links);
			//渲染节点
			var node=this.drawnode(this.graph.nodes);
			//渲染节点的文字
			var nodetext=this.drawnodetext(this.graph.nodes)
			var zoom = d3.behavior.zoom()
				.scaleExtent([0, 10])
				.on("zoom", function() {
					node.attr("transform","translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
					link.attr("transform","translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
					linktext.attr("transform","translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
					nodetext.attr("transform","translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
				}); 
			svg.call(zoom);
			svg.on("dblclick.zoom",null); //静止双击缩放
			this.forcetick(link,linktext,node,nodetext);
	        force.stop();
	        force.start();        
	   }
	 var contextmenu= function(d) {
		 if(typeof(d)!='undefined'){
			 app.graphEntity=d;
			 debugger;
			 var position = d3.mouse(this);
			 var xx=document.body.scrollTop;
			 app.selectnodeid=d.uuid;
			   d3.select('#my_custom_menu')
			     .style('position', 'absolute')
			     .style('left', position[0]+300 + "px")
			     .style('top', position[1] +154+ document.body.scrollTop + "px") 
			     .style('display', 'block');
			   d3.event.preventDefault();
		 }
		}
	 var getDataSourcelist=function(){
			var _this=this;
			var ajaxdata = [];
			$.ajax({
		           data: ajaxdata,
		           type: "POST",
		           url: contextRoot+"datasource/getDataSourcelist",
		           success: function (result) {
		               if (result.code == 200) {
		            	   _this.dataSourceList=result.data;
		               }
		           },
		           error: function (data) {}
		       });
		}
	 
	var app = new Vue({
		el: '#app',
		data:{
			radio1:'',
			radio2:'',
			tips:'',
			activeName:'',
			dataconfigactive:'',
			querywords:'',
			isedit:false,
			isaddnode:false,
			isaddlink:false,
			isdeletelink:false,
			updateshipid:0,
			selectnodeid:0,
			domain:'',
			selectnode:{
				name:'',
				count:0
			},
			graph:{
				nodes:[],
				links:[]
			},
			graphEntity:{
				uuid:0,
				name:'',
				field:'',
				entitytype:0,
				showstyle:0,
				unit:'',
				sql:'',
				table:'',
				detailshowfield:'',
				targettable:'',
				sortcode:'',
				filter:[]
			},
			dataSourceList:[],
			fieldDataList:[],
			domainnodes:[],
			domainpropnodes:[],
			domainmethodnodes:[],
			domainentitynodes:[],
			pageModel:{
				pageIndex:1,
				pageSize:10,
				totalCount:0,
            	totalPage:0,
            	nodeList:[]
			},
			handleCurrentChange(val) {// 分页点击
        	    this.pageModel.pageIndex = val;
        	    this.getPageData();
        	},
        	handleSizeChange(val) {
        		this.pageModel.pageIndex = 1;
        		this.pageModel.pageSize = val;
        		this.getPageData();
       	    }
		},
		mounted(){
			
		},
		computed:{
			graphfilter:{
				get:function(){
					debugger;
					if(this.graphEntity.filter.length>0){
						var f=this.graphEntity.filter;
						var val= f.join(',');
						this.graphfiltervalue=val;
						return val;
					}
					return "";
					
				},
				set:function(value){
					if(value.length>0){
						//debugger;
						var val= value.split(',');
						this.graphEntity.filter=val;
						return val;
					}
					return [];
				},
			}
		},
		filters : {
			filterMethod(){
				debugger;
				var val= this.graphEntity.filter.join(';');
				return val;
			}
		},
		created(){
			this.initdomain();
			this.getdomaingraph();
			this.getdomainnodes();
			this.getDataSourcelist();
		},
		methods: {
			initdomain:initdomain,
			getdomaingraph:getdomaingraph,
			getdomainnodes:getdomainnodes,
			getcurrentnodeinfo:getcurrentnodeinfo,
			getDataSourcelist:getDataSourcelist,
			loadChart:loadChart,
			contextmenu:contextmenu,
			btnedit(event){
				this.isedit=true;
				$('#my_custom_menu').hide();
			},
			btnaddlink(event){
				this.isaddlink=true;
				this.tips="你正在添加连线,先选择的节点为源节点,后选择的节点为目标节点";
			},
			btndeletelink(){
				this.isdeletelink=true;
				this.tips="你正在删除连线,双击连线进行删除";
				d3.select('.link').attr("class", "link linkdelete"); //修改鼠标样式为"+"
			},
			btnaddnode(){
				this.isaddnode=true;
				this.isedit=true;
				this.resetentity();
			},
			btndeletenode(){
				$('#my_custom_menu').hide();
				this.$confirm('此操作将删除该节点及周边关系(不可恢复), 是否继续?', '三思而后行', {
			          confirmButtonText: '确定',
			          cancelButtonText: '取消',
			          type: 'warning'
			        }).then(() => {
			        	this.deletenode();
			        }).catch(() => {
			          this.$message({
			            type: 'info',
			            message: '已取消删除'
			          });          
			        });
			},
			selecteddatasource(item,index){
            	for (var i = 0; i < this.dataSourceList.length; i++) {
            		if(i!=index){
            			this.dataSourceList[i].dbopenstate=false;
	            		this.dataSourceList[i].selected =false;
            		}
				}
            	item.dbopenstate=!item.dbopenstate;
            	item.selected =!item.selected;
            },
			getFieldlist(t,tableList){
				t.selected=!t.selected;
				var _this=this;
				_this.graphEntity.table=t.name;
				var ajaxdata = {};
				ajaxdata.categoryId=t.id;
				$.ajax({
			           data: ajaxdata,
			           type: "POST",
			           url: contextRoot+"datasource/getFieldlist",
			           success: function (result) {
			               if (result.code == 200) {
			            	   _this.fieldDataList=result.data;
			               }
			           },
			           error: function (data) {}
			       });
			},
			selectfield(f){
				var _this=this;
				_this.graphEntity.field=f.fieldname;
			},
			gettablelist(db){
				if(!db.tbopenstate){
					db.tbopenstate=true;
					var _this=this;
					var ajaxdata = {};
					ajaxdata.sourceId=db.id;
					$.ajax({
				           data: ajaxdata,
				           type: "POST",
				           url: contextRoot+"datasource/getTablelist",
				           success: function (result) {
				               if (result.code == 200) {
				            	   db.tableList=result.data;
				               }
				           },
				           error: function (data) {}
				       });
				}else{
					db.tbopenstate=false;
				}
			},
			checkIselected(name){
            	if(this.querywords!=''&&name.indexOf(this.querywords) >=0){
            		return true;
            	}
            	return false;
            },
			createnode:createnode,
			addmaker:addmaker,
			addmaker2:addmaker2,
			drawnode:drawnode,
			drawnodetext:drawnodetext,
			drawlink:drawlink,
			drawlinktext:drawlinktext,
			deletenode:deletenode,
			deletelink:deletelink,
			createlink:createlink,
			forcetick:forcetick,
			updatelinkName:updatelinkName,
			showlinkprop(){
				 this.$prompt('请输入关系名称', '提示', {
			          confirmButtonText: '确定',
			          cancelButtonText: '取消'
			        }).then(({ value }) => {
			          this.updatelinkName(value);
			        }).catch(() => {
			          this.$message({
			            type: 'info',
			            message: '取消输入'
			          });       
			        });
			 },
			 resetsubmit(){
				this.isaddnode=false;
				this.isedit=false;
				this.resetentity();
				this.fieldDataList=[];
				this.dataconfigactive='';
			 },
			 resetentity(){
				 this.graphEntity={
						uuid:0,
						name:'',
						field:'',
						entitytype:0,
						showstyle:0,
						unit:'',
						sql:'',
						table:'',
						detailshowfield:'',
						targettable:'',
						sortcode:'',
						filter:[]
					};
			 }
		}
	  })
	 $("body").bind("mousedown", function(event){
		 if (!(event.target.id === "my_custom_menu" || $(event.target).parents("#my_custom_menu").length > 0)) {
             $("#my_custom_menu").hide();
         }
	 });