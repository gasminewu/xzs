(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-a995134a"],{b067:function(e,t,n){"use strict";var o=n("b775");t["a"]={pageList:function(e){return Object(o["a"])("/api/admin/book/page",e)},edit:function(e){return Object(o["a"])("/api/admin/book/edit",e)},select:function(e){return Object(o["a"])("/api/admin/book/select/"+e)},deletebook:function(e){return Object(o["a"])("/api/admin/book/delete/"+e)},taskInit:function(e){return Object(o["a"])("/api/admin/book/taskInit",e)},importinsertFullBook:function(e){return Object(o["a"])("/api/admin/book/importinsertFullBook",e)}}},dffa:function(e,t,n){"use strict";n.r(t);var o=function(){var e=this,t=e._self._c;return t("div",{staticClass:"app-container"},[t("el-form",{directives:[{name:"loading",rawName:"v-loading",value:e.formLoading,expression:"formLoading"}],ref:"form",attrs:{model:e.form,"label-width":"100px",rules:e.rules}},[t("el-form-item",{attrs:{label:"内容：",prop:"finishcontent"}},[t("el-input",{attrs:{type:"textarea",rows:"13"},model:{value:e.form.finishcontent,callback:function(t){e.$set(e.form,"finishcontent",t)},expression:"form.finishcontent"}})],1),t("el-form-item",{attrs:{label:"状态：",required:""}},[t("el-select",{attrs:{placeholder:"状态"},model:{value:e.form.status,callback:function(t){e.$set(e.form,"status",t)},expression:"form.status"}},e._l(e.statusEnum,(function(e){return t("el-option",{key:e.key,attrs:{value:e.key,label:e.value}})})),1)],1),t("el-form-item",[t("el-button",{attrs:{type:"primary"},on:{click:e.submitForm}},[e._v("提交")])],1)],1)],1)},r=[],a=n("5530"),i=(n("14d9"),n("2f62")),s=n("b067"),u={data:function(){return{form:{finishcontent:"",status:null},formLoading:!1,rules:{status:[{required:!0,message:"请选择状态",trigger:"change"}]}}},created:function(){var e=this.$route.query.id,t=this;e&&0!==parseInt(e)&&(t.formLoading=!0,s["a"].select(e).then((function(e){t.form=e.response,t.formLoading=!1})))},methods:Object(a["a"])({submitForm:function(){var e=this,t=this;this.$refs.form.validate((function(n){if(!n)return!1;e.formLoading=!0,s["a"].edit(e.form).then((function(n){1===n.code?(t.$message.success(n.message),t.delCurrentView(t).then((function(){t.$router.push("/book/list")}))):(t.$message.error(n.message),e.formLoading=!1)})).catch((function(t){e.formLoading=!1}))}))}},Object(i["b"])("tagsView",{delCurrentView:"delCurrentView"})),computed:Object(a["a"])(Object(a["a"])({},Object(i["c"])("enumItem",["enumFormat"])),Object(i["e"])("enumItem",{statusEnum:function(e){return e.book.statusEnum}}))},c=u,l=n("2877"),m=Object(l["a"])(c,o,r,!1,null,null,null);t["default"]=m.exports}}]);