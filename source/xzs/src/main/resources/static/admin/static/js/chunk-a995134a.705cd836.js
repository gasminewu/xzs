(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-a995134a"],{b067:function(t,e,n){"use strict";var o=n("b775");e["a"]={pageList:function(t){return Object(o["a"])("/api/admin/book/page",t)},edit:function(t){return Object(o["a"])("/api/admin/book/edit",t)},select:function(t){return Object(o["a"])("/api/admin/book/select/"+t)},deletebook:function(t){return Object(o["a"])("/api/admin/book/delete/"+t)},taskInit:function(t){return Object(o["a"])("/api/admin/book/taskInit",t)},importinsertFullBook:function(t){return Object(o["a"])("/api/admin/book/importinsertFullBook",t)},updateSelectionStatus:function(t){return Object(o["a"])("/api/admin/book/updateSelectionStatus",t)}}},dffa:function(t,e,n){"use strict";n.r(e);var o=function(){var t=this,e=t._self._c;return e("div",{staticClass:"app-container"},[e("el-form",{directives:[{name:"loading",rawName:"v-loading",value:t.formLoading,expression:"formLoading"}],ref:"form",attrs:{model:t.form,"label-width":"100px",rules:t.rules}},[e("el-form-item",{attrs:{label:"内容：",prop:"finishcontent"}},[e("el-input",{attrs:{type:"textarea",rows:"13"},model:{value:t.form.finishcontent,callback:function(e){t.$set(t.form,"finishcontent",e)},expression:"form.finishcontent"}})],1),e("el-form-item",{attrs:{label:"状态：",required:""}},[e("el-select",{attrs:{placeholder:"状态"},model:{value:t.form.status,callback:function(e){t.$set(t.form,"status",e)},expression:"form.status"}},t._l(t.statusEnum,(function(t){return e("el-option",{key:t.key,attrs:{value:t.key,label:t.value}})})),1)],1),e("el-form-item",[e("el-button",{attrs:{type:"primary"},on:{click:t.submitForm}},[t._v("提交")])],1)],1)],1)},a=[],r=n("5530"),i=(n("14d9"),n("2f62")),s=n("b067"),u={data:function(){return{form:{finishcontent:"",status:null},formLoading:!1,rules:{status:[{required:!0,message:"请选择状态",trigger:"change"}]}}},created:function(){var t=this.$route.query.id,e=this;t&&0!==parseInt(t)&&(e.formLoading=!0,s["a"].select(t).then((function(t){e.form=t.response,e.formLoading=!1})))},methods:Object(r["a"])({submitForm:function(){var t=this,e=this;this.$refs.form.validate((function(n){if(!n)return!1;t.formLoading=!0,s["a"].edit(t.form).then((function(n){1===n.code?(e.$message.success(n.message),e.delCurrentView(e).then((function(){e.$router.push("/book/list")}))):(e.$message.error(n.message),t.formLoading=!1)})).catch((function(e){t.formLoading=!1}))}))}},Object(i["b"])("tagsView",{delCurrentView:"delCurrentView"})),computed:Object(r["a"])(Object(r["a"])({},Object(i["c"])("enumItem",["enumFormat"])),Object(i["e"])("enumItem",{statusEnum:function(t){return t.book.statusEnum}}))},c=u,l=n("2877"),m=Object(l["a"])(c,o,a,!1,null,null,null);e["default"]=m.exports}}]);