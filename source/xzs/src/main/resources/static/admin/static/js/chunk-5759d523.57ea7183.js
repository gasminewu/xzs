(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-5759d523"],{"082c":function(e,t,a){},"129f":function(e,t,a){"use strict";e.exports=Object.is||function(e,t){return e===t?0!==e||1/e===1/t:e!=e&&t!=t}},"333d":function(e,t,a){"use strict";var r=function(){var e=this,t=e._self._c;return t("div",{staticClass:"pagination-container",class:{hidden:e.hidden}},[t("el-pagination",e._b({attrs:{background:e.background,"current-page":e.currentPage,"page-size":e.pageSize,layout:e.layout,"page-sizes":e.pageSizes,total:e.total},on:{"update:currentPage":function(t){e.currentPage=t},"update:current-page":function(t){e.currentPage=t},"update:pageSize":function(t){e.pageSize=t},"update:page-size":function(t){e.pageSize=t},"size-change":e.handleSizeChange,"current-change":e.handleCurrentChange}},"el-pagination",e.$attrs,!1))],1)},o=[];a("a9e3");Math.easeInOutQuad=function(e,t,a,r){return e/=r/2,e<1?a/2*e*e+t:(e--,-a/2*(e*(e-2)-1)+t)};var i=function(){return window.requestAnimationFrame||window.webkitRequestAnimationFrame||window.mozRequestAnimationFrame||function(e){window.setTimeout(e,1e3/60)}}();function n(e){document.documentElement.scrollTop=e,document.body.parentNode.scrollTop=e,document.body.scrollTop=e}function l(){return document.documentElement.scrollTop||document.body.parentNode.scrollTop||document.body.scrollTop}function s(e,t,a){var r=l(),o=e-r,s=20,u=0;t="undefined"===typeof t?500:t;var c=function e(){u+=s;var l=Math.easeInOutQuad(u,r,o,t);n(l),u<t?i(e):a&&"function"===typeof a&&a()};c()}var u={name:"Pagination",props:{total:{required:!0,type:Number},page:{type:Number,default:1},limit:{type:Number,default:10},pageSizes:{type:Array,default:function(){return[5,10,20,30,50]}},layout:{type:String,default:"total, sizes, prev, pager, next, jumper"},background:{type:Boolean,default:!0},autoScroll:{type:Boolean,default:!0},hidden:{type:Boolean,default:!1}},computed:{currentPage:{get:function(){return this.page},set:function(e){this.$emit("update:page",e)}},pageSize:{get:function(){return this.limit},set:function(e){this.$emit("update:limit",e)}}},methods:{handleSizeChange:function(e){this.$emit("pagination",{page:this.currentPage,limit:e}),this.autoScroll&&s(0,800)},handleCurrentChange:function(e){this.$emit("pagination",{page:e,limit:this.pageSize}),this.autoScroll&&s(0,800)}}},c=u,p=(a("d05f"),a("2877")),m=Object(p["a"])(c,r,o,!1,null,"90fd946a",null);t["a"]=m.exports},"4a0c":function(e,t,a){"use strict";var r=a("b775");t["a"]={pageList:function(e){return Object(r["a"])("/api/admin/exam/paper/page",e)},taskExamPage:function(e){return Object(r["a"])("/api/admin/exam/paper/taskExamPage",e)},edit:function(e){return Object(r["a"])("/api/admin/exam/paper/edit",e)},select:function(e){return Object(r["a"])("/api/admin/exam/paper/select/"+e)},deletePaper:function(e){return Object(r["a"])("/api/admin/exam/paper/delete/"+e)}}},"63f4":function(e,t,a){"use strict";var r=function(){var e=this,t=e._self._c;return t("div",[t("script",{staticStyle:{height:"300px"},attrs:{id:e.randomId,type:"text/plain"}})])},o=[],i={name:"UE",props:{value:{default:function(){return""}}},data:function(){return{randomId:"editor_"+1e17*Math.random(),instance:null,ready:!1}},watch:{value:function(e,t){null!=e&&this.ready&&(this.instance=UE.getEditor(this.randomId),this.instance.setContent(e))}},mounted:function(){this.initEditor()},beforeDestroy:function(){null!==this.instance&&this.instance.destroy&&this.instance.destroy()},methods:{initEditor:function(){var e=this;this.$nextTick((function(){e.instance=UE.getEditor(e.randomId),e.instance.addListener("ready",(function(){e.ready=!0,e.$emit("ready",e.instance)}))}))},getUEContent:function(){return this.instance.getContent()},setText:function(e){this.instance=UE.getEditor(this.randomId),this.instance.setContent(e)}}},n=i,l=a("2877"),s=Object(l["a"])(n,r,o,!1,null,null,null);t["a"]=s.exports},"81d0":function(e,t,a){"use strict";a.r(t);a("b0c0"),a("ac1f"),a("841c");var r=function(){var e=this,t=e._self._c;return t("div",{staticClass:"app-container"},[t("el-form",{directives:[{name:"loading",rawName:"v-loading",value:e.formLoading,expression:"formLoading"}],ref:"form",attrs:{model:e.form,"label-width":"100px",rules:e.rules}},[t("el-form-item",{attrs:{label:"阶段：",prop:"gradeLevel",required:""}},[t("el-select",{attrs:{placeholder:"阶段"},on:{change:e.levelChange},model:{value:e.form.gradeLevel,callback:function(t){e.$set(e.form,"gradeLevel",t)},expression:"form.gradeLevel"}},e._l(e.levelEnum,(function(e){return t("el-option",{key:e.key,attrs:{value:e.key,label:e.value}})})),1)],1),t("el-form-item",{attrs:{label:"标题：",prop:"title",required:""}},[t("el-input",{model:{value:e.form.title,callback:function(t){e.$set(e.form,"title",t)},expression:"form.title"}})],1),t("el-form-item",{attrs:{label:"优先级：",prop:"priority",required:""}},[t("el-select",{attrs:{placeholder:"优先级",clearable:""},model:{value:e.form.priority,callback:function(t){e.$set(e.form,"priority",t)},expression:"form.priority"}},e._l(e.priorityEnum,(function(e){return t("el-option",{key:e.key,attrs:{value:e.key,label:e.value}})})),1)],1),t("el-form-item",{attrs:{label:"时间限制：",prop:"limitDateTime",required:""}},[t("el-date-picker",{attrs:{"value-format":"yyyy-MM-dd HH:mm:ss",type:"datetimerange","range-separator":"至","start-placeholder":"开始日期","end-placeholder":"结束日期"},model:{value:e.form.limitDateTime,callback:function(t){e.$set(e.form,"limitDateTime",t)},expression:"form.limitDateTime"}})],1),t("el-form-item",{attrs:{label:"进度(%)：",prop:"process"}},[t("el-input-number",{attrs:{precision:0,step:1,max:100},model:{value:e.form.process,callback:function(t){e.$set(e.form,"process",t)},expression:"form.process"}})],1),t("el-form-item",{attrs:{label:"顺序号：",prop:"sn",required:""}},[t("el-input-number",{attrs:{precision:0,step:1,max:100},model:{value:e.form.sn,callback:function(t){e.$set(e.form,"sn",t)},expression:"form.sn"}})],1),t("el-form-item",{attrs:{label:"任务描述：",prop:"taskcontent"}},[t("el-input",{on:{focus:function(t){return e.inputClick(e.form,"taskcontent")}},model:{value:e.form.taskcontent,callback:function(t){e.$set(e.form,"taskcontent",t)},expression:"form.taskcontent"}})],1),t("el-form-item",{attrs:{label:"状态：",prop:"status",required:""}},[t("el-select",{attrs:{placeholder:"状态",clearable:""},model:{value:e.form.status,callback:function(t){e.$set(e.form,"status",t)},expression:"form.status"}},e._l(e.statusEnum,(function(e){return t("el-option",{key:e.key,attrs:{value:e.key,label:e.value}})})),1)],1),t("el-form-item",{attrs:{label:"任务类别：",prop:"tasktype",required:""}},[t("el-select",{attrs:{placeholder:"任务类别",clearable:""},model:{value:e.form.tasktype,callback:function(t){e.$set(e.form,"tasktype",t)},expression:"form.tasktype"}},e._l(e.taskTypeEnum,(function(e){return t("el-option",{key:e.key,attrs:{value:e.key,label:e.value}})})),1)],1),t("el-form-item",{directives:[{name:"show",rawName:"v-show",value:1===e.form.tasktype,expression:"form.tasktype===1"}],attrs:{label:"知识点：",required:""}},[t("el-table",{staticStyle:{width:"100%"},attrs:{data:e.form.bookItems,border:"",fit:"","highlight-current-row":""}},[t("el-table-column",{attrs:{prop:"subjectId",label:"模块",formatter:e.subjectFormatter,width:"120px"}}),t("el-table-column",{attrs:{prop:"title",label:"知识点"}}),t("el-table-column",{attrs:{prop:"status",label:"状态",formatter:e.statusFormatter,width:"80px"}}),t("el-table-column",{attrs:{prop:"createTime",label:"创建时间",width:"150px"}}),t("el-table-column",{attrs:{label:"操作",align:"center",width:"160px"},scopedSlots:e._u([{key:"default",fn:function(a){var r=a.row;return[t("el-button",{staticClass:"link-left",attrs:{size:"mini",type:"danger"},on:{click:function(t){return e.removeBook(r)}}},[e._v("删除")])]}}])})],1)],1),t("el-form-item",{directives:[{name:"show",rawName:"v-show",value:3===e.form.tasktype,expression:"form.tasktype===3"}],attrs:{label:"试卷：",required:""}},[t("el-table",{staticStyle:{width:"100%"},attrs:{data:e.form.paperItems,border:"",fit:"","highlight-current-row":""}},[t("el-table-column",{attrs:{prop:"subjectId",label:"模块",formatter:e.subjectFormatter,width:"120px"}}),t("el-table-column",{attrs:{prop:"name",label:"名称"}}),t("el-table-column",{attrs:{label:"操作",align:"center",width:"160px"},scopedSlots:e._u([{key:"default",fn:function(a){var r=a.row;return[t("el-button",{staticClass:"link-left",attrs:{size:"mini",type:"danger"},on:{click:function(t){return e.removePaper(r)}}},[e._v("删除")])]}}])})],1)],1),t("el-form-item",[t("el-button",{attrs:{type:"primary"},on:{click:e.submitForm}},[e._v("提交")]),t("el-button",{on:{click:e.resetForm}},[e._v("重置")]),t("el-button",{directives:[{name:"show",rawName:"v-show",value:3===e.form.tasktype,expression:"form.tasktype===3"}],attrs:{type:"success"},on:{click:e.addPaper}},[e._v("添加试卷")]),t("el-button",{directives:[{name:"show",rawName:"v-show",value:1===e.form.tasktype,expression:"form.tasktype===1"}],attrs:{type:"success"},on:{click:e.addBook}},[e._v("添加知识点")])],1)],1),t("el-dialog",{attrs:{visible:e.bookPage.showDialog,width:"70%"},on:{"update:visible":function(t){return e.$set(e.bookPage,"showDialog",t)}}},[t("el-form",{ref:"queryForm",attrs:{model:e.bookPage.queryParam,inline:!0}},[t("el-form-item",{attrs:{label:"模块："}},[t("el-select",{attrs:{clearable:""},model:{value:e.bookPage.queryParam.subjectId,callback:function(t){e.$set(e.bookPage.queryParam,"subjectId",t)},expression:"bookPage.queryParam.subjectId"}},e._l(e.subjectFilter,(function(e){return t("el-option",{key:e.id,attrs:{value:e.id,label:e.name+" ( "+e.levelName+" )"}})})),1)],1),t("el-form-item",{attrs:{label:"状态："}},[t("el-select",{attrs:{clearable:""},model:{value:e.bookPage.queryParam.status,callback:function(t){e.$set(e.bookPage.queryParam,"status",t)},expression:"bookPage.queryParam.status"}},e._l(e.bookstatusEnum,(function(e){return t("el-option",{key:e.key,attrs:{value:e.key,label:e.value}})})),1)],1),t("el-form-item",[t("el-button",{attrs:{type:"primary"},on:{click:e.examBookSubmitForm}},[e._v("查询")])],1)],1),t("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.bookPage.listLoading,expression:"bookPage.listLoading"}],staticStyle:{width:"100%"},attrs:{data:e.bookPage.tableData,border:"",fit:"","highlight-current-row":""},on:{"selection-change":e.handleSelectionChangeBook}},[t("el-table-column",{attrs:{type:"selection",width:"45"}}),t("el-table-column",{attrs:{prop:"subjectId",label:"模块",formatter:e.subjectFormatter,width:"150px"}}),t("el-table-column",{attrs:{prop:"title",label:"知识点"}}),t("el-table-column",{attrs:{prop:"status",label:"状态",formatter:e.statusFormatter,width:"80px"}}),t("el-table-column",{attrs:{prop:"createTime",label:"创建时间",width:"150px"}})],1),t("pagination",{directives:[{name:"show",rawName:"v-show",value:e.bookPage.total>0,expression:"bookPage.total>0"}],attrs:{total:e.bookPage.total,page:e.bookPage.queryParam.pageIndex,limit:e.bookPage.queryParam.pageSize},on:{"update:page":function(t){return e.$set(e.bookPage.queryParam,"pageIndex",t)},"update:limit":function(t){return e.$set(e.bookPage.queryParam,"pageSize",t)},pagination:e.searchBook}}),t("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[t("el-button",{on:{click:function(t){e.bookPage.showDialog=!1}}},[e._v("取 消")]),t("el-button",{attrs:{type:"primary"},on:{click:e.confirmBookSelect}},[e._v("确定")])],1)],1),t("el-dialog",{attrs:{visible:e.paperPage.showDialog,width:"70%"},on:{"update:visible":function(t){return e.$set(e.paperPage,"showDialog",t)}}},[t("el-form",{ref:"queryForm",attrs:{model:e.paperPage.queryParam,inline:!0}},[t("el-form-item",{attrs:{label:"模块："}},[t("el-select",{attrs:{clearable:""},model:{value:e.paperPage.queryParam.subjectId,callback:function(t){e.$set(e.paperPage.queryParam,"subjectId",t)},expression:"paperPage.queryParam.subjectId"}},e._l(e.subjectFilter,(function(e){return t("el-option",{key:e.id,attrs:{value:e.id,label:e.name+" ( "+e.levelName+" )"}})})),1)],1),t("el-form-item",[t("el-button",{attrs:{type:"primary"},on:{click:e.examPaperSubmitForm}},[e._v("查询")])],1)],1),t("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.paperPage.listLoading,expression:"paperPage.listLoading"}],staticStyle:{width:"100%"},attrs:{data:e.paperPage.tableData,border:"",fit:"","highlight-current-row":""},on:{"selection-change":e.handleSelectionChange}},[t("el-table-column",{attrs:{type:"selection",width:"55"}}),t("el-table-column",{attrs:{prop:"id",label:"Id",width:"90px"}}),t("el-table-column",{attrs:{prop:"subjectId",label:"模块",formatter:e.subjectFormatter,width:"120px"}}),t("el-table-column",{attrs:{prop:"name",label:"名称"}}),t("el-table-column",{attrs:{prop:"createTime",label:"创建时间",width:"160px"}})],1),t("pagination",{directives:[{name:"show",rawName:"v-show",value:e.paperPage.total>0,expression:"paperPage.total>0"}],attrs:{total:e.paperPage.total,page:e.paperPage.queryParam.pageIndex,limit:e.paperPage.queryParam.pageSize},on:{"update:page":function(t){return e.$set(e.paperPage.queryParam,"pageIndex",t)},"update:limit":function(t){return e.$set(e.paperPage.queryParam,"pageSize",t)},pagination:e.search}}),t("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[t("el-button",{on:{click:function(t){e.paperPage.showDialog=!1}}},[e._v("取 消")]),t("el-button",{attrs:{type:"primary"},on:{click:e.confirmPaperSelect}},[e._v("确定")])],1)],1),t("el-dialog",{staticStyle:{width:"100%",height:"100%"},attrs:{visible:e.richEditor.dialogVisible,"append-to-body":"","close-on-click-modal":!1,"show-close":!1,center:""},on:{"update:visible":function(t){return e.$set(e.richEditor,"dialogVisible",t)}}},[t("Ueditor",{on:{ready:e.editorReady}}),t("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[t("el-button",{attrs:{type:"primary"},on:{click:e.editorConfirm}},[e._v("确 定")]),t("el-button",{on:{click:function(t){e.richEditor.dialogVisible=!1}}},[e._v("取 消")])],1)],1)],1)},o=[],i=a("5530"),n=(a("d3b7"),a("159b"),a("14d9"),a("4de4"),a("a434"),a("63f4")),l=a("b199"),s=a("4a0c"),u=a("b067"),c=a("333d"),p=a("2f62"),m={components:{Pagination:c["a"],Ueditor:n["a"]},data:function(){return{form:{id:null,gradeLevel:null,priority:1,tasktype:0,status:1,title:"",process:0,limitDateTime:[],sn:1,bookItems:[],paperItems:[]},subjectFilter:null,formLoading:!1,paperPage:{multipleSelection:[],showDialog:!1,queryParam:{subjectId:null,level:null,pageIndex:1,pageSize:5},listLoading:!0,tableData:[],total:0},bookPage:{multipleSelection:[],showDialog:!1,queryParam:{subjectId:null,level:null,status:1,pageIndex:1,pageSize:5},listLoading:!0,tableData:[],total:0},rules:{gradeLevel:[{required:!0,message:"请输入项目",trigger:"change"}],priority:[{required:!0,message:"请输入优先级",trigger:"change"}],taskType:[{required:!0,message:"请输入任务类别",trigger:"change"}],title:[{required:!0,message:"请输入任务标题",trigger:"blur"}]},richEditor:{dialogVisible:!1,object:null,parameterName:"",instance:null}}},created:function(){var e=this;this.initSubject((function(){e.subjectFilter=e.subjects}));var t=this.$route.query.id;t&&0!==parseInt(t)&&(e.formLoading=!0,l["a"].select(t).then((function(t){e.form=t.response,e.formLoading=!1})))},methods:Object(i["a"])(Object(i["a"])({editorReady:function(e){this.richEditor.instance=e;var t=this.richEditor.object[this.richEditor.parameterName];this.richEditor.instance.setContent(t),this.richEditor.instance.focus(!0)},inputClick:function(e,t){this.richEditor.object=e,this.richEditor.parameterName=t,this.richEditor.dialogVisible=!0},editorConfirm:function(){var e=this.richEditor.instance.getContent();this.richEditor.object[this.richEditor.parameterName]=e,this.richEditor.dialogVisible=!1},addPaper:function(){this.paperPage.queryParam.level=this.form.gradeLevel,this.paperPage.showDialog=!0,this.search()},addBook:function(){this.bookPage.queryParam.level=this.form.gradeLevel,this.bookPage.showDialog=!0,this.searchBook()},confirmPaperSelect:function(){var e=this;this.paperPage.multipleSelection.forEach((function(t){return e.form.paperItems.push(t)})),this.paperPage.showDialog=!1},confirmBookSelect:function(){var e=this;this.bookPage.multipleSelection.forEach((function(t){return e.form.bookItems.push(t)})),this.bookPage.showDialog=!1},search:function(){var e=this;this.paperPage.showDialog=!0,this.listLoading=!0,s["a"].taskExamPage(this.paperPage.queryParam).then((function(t){var a=t.response;e.paperPage.tableData=a.list,e.paperPage.total=a.total,e.paperPage.queryParam.pageIndex=a.pageNum,e.paperPage.listLoading=!1}))},searchBook:function(){var e=this;this.bookPage.showDialog=!0,this.listLoading=!0,u["a"].pageList(this.bookPage.queryParam).then((function(t){var a=t.response;e.bookPage.tableData=a.list,e.bookPage.total=a.total,e.bookPage.queryParam.pageIndex=a.pageNum,e.bookPage.listLoading=!1}))},handleSelectionChange:function(e){this.paperPage.multipleSelection=e},handleSelectionChangeBook:function(e){this.bookPage.multipleSelection=e},examPaperSubmitForm:function(){this.bookPage.queryParam.pageIndex=1,this.search()},examBookSubmitForm:function(){this.paperPage.queryParam.pageIndex=1,this.searchBook()},levelChange:function(){var e=this;this.paperPage.queryParam.subjectId=null,this.bookPage.queryParam.subjectId=null,this.subjectFilter=this.subjects.filter((function(t){return t.level===e.form.gradeLevel}))},removePaper:function(e){this.form.paperItems.forEach((function(t,a,r){t.id===e.id&&r.splice(a,1)}))},removeBook:function(e){this.form.bookItems.forEach((function(t,a,r){t.id===e.id&&r.splice(a,1)}))},submitForm:function(){var e=this,t=this;this.$refs.form.validate((function(a){if(!a)return!1;e.formLoading=!0,l["a"].edit(e.form).then((function(e){1===e.code?(t.$message.success(e.message),t.delCurrentView(t).then((function(){t.$router.push("/day/list")}))):t.$message.error(e.message),t.formLoading=!1})).catch((function(e){t.formLoading=!1}))}))},pinyinFormatter:function(e,t,a,r){return this.enumFormat(this.pinyinEnum,a)},statusFormatter:function(e,t,a,r){return this.enumFormat(this.bookstatusEnum,a)},setFormatter:function(e,t,a,r){return a&&0!==parseInt(a)?"是":"否"},resetForm:function(){var e=this.form.id;this.$refs["form"].resetFields(),this.form={id:null,gradeLevel:null,priority:1,tasktype:0,status:1,title:"",process:0,sn:1,bookItems:[],paperItems:[]},this.form.id=e},subjectFormatter:function(e,t,a,r){return this.subjectEnumFormat(a)}},Object(p["b"])("exam",{initSubject:"initSubject"})),Object(p["b"])("tagsView",{delCurrentView:"delCurrentView"})),computed:Object(i["a"])(Object(i["a"])(Object(i["a"])(Object(i["a"])({},Object(p["c"])("enumItem",["enumFormat"])),Object(p["e"])("enumItem",{priorityEnum:function(e){return e.task.priorityEnum},statusEnum:function(e){return e.task.statusEnum},taskTypeEnum:function(e){return e.task.taskTypeEnum},pinyinEnum:function(e){return e.book.pinyinEnum},bookstatusEnum:function(e){return e.book.statusEnum},levelEnum:function(e){return e.user.levelEnum}})),Object(p["c"])("exam",["subjectEnumFormat"])),Object(p["e"])("exam",{subjects:function(e){return e.subjects}}))},d=m,f=a("2877"),b=Object(f["a"])(d,r,o,!1,null,null,null);t["default"]=b.exports},"841c":function(e,t,a){"use strict";var r=a("c65b"),o=a("d784"),i=a("825a"),n=a("7234"),l=a("1d80"),s=a("129f"),u=a("577e"),c=a("dc4a"),p=a("14c3");o("search",(function(e,t,a){return[function(t){var a=l(this),o=n(t)?void 0:c(t,e);return o?r(o,t,a):new RegExp(t)[e](u(a))},function(e){var r=i(this),o=u(e),n=a(t,r,o);if(n.done)return n.value;var l=r.lastIndex;s(l,0)||(r.lastIndex=0);var c=p(r,o);return s(r.lastIndex,l)||(r.lastIndex=l),null===c?-1:c.index}]}))},b067:function(e,t,a){"use strict";var r=a("b775");t["a"]={pageList:function(e){return Object(r["a"])("/api/admin/book/page",e)},edit:function(e){return Object(r["a"])("/api/admin/book/edit",e)},select:function(e){return Object(r["a"])("/api/admin/book/select/"+e)},deletebook:function(e){return Object(r["a"])("/api/admin/book/delete/"+e)},taskInit:function(e){return Object(r["a"])("/api/admin/book/taskInit",e)},importinsertFullBook:function(e){return Object(r["a"])("/api/admin/book/importinsertFullBook",e)}}},b199:function(e,t,a){"use strict";var r=a("b775");t["a"]={pageList:function(e){return Object(r["a"])("/api/admin/task/page",e)},edit:function(e){return Object(r["a"])("/api/admin/task/edit",e)},select:function(e){return Object(r["a"])("/api/admin/task/select/"+e)},exportList:function(e){return Object(r["a"])("/api/admin/task/export",e)},deleteTask:function(e){return Object(r["a"])("/api/admin/task/delete/"+e)}}},d05f:function(e,t,a){"use strict";a("082c")}}]);