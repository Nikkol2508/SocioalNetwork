webpackJsonp([7],{dPCo:function(e,t){},tJ7h:function(e,t,i){"use strict";var o=i("Dd8w"),n=i.n(o),s=i("NYxO"),a=i("/o5o"),r=(i("PJh5"),{name:"ProfileInfo",components:{Modal:a.a},props:{me:Boolean,online:Boolean,blocked:Boolean,friend:String,meBlocked:Boolean,info:Object},data:function(){return{modalShow:!1,modalText:"",modalType:"deleteFriend"}},computed:n()({},Object(s.c)("profile/dialogs",["dialogs"]),{statusText:function(){return this.online?"онлайн":"не в сети"},blockedText:function(){return this.blocked?"Пользователь заблокирован":"Заблокировать"},btnVariantInfo:function(){return this.blocked?{variant:"red",text:"Разблокировать"}:"FRIEND"===this.friend?{variant:"red",text:"Удалить из друзей"}:"REQUEST_RECEIVED"===this.friend?{variant:"blue",text:"Запрос в друзья"}:"REQUEST_SENT"===this.friend?{variant:"white",text:"Отменить запрос в друзья"}:{variant:"white",text:"Добавить в друзья"}}}),methods:n()({},Object(s.b)("users/actions",["apiBlockUser","apiUnblockUser"]),Object(s.b)("profile/friends",["apiAddFriends","apiDeleteFriends","apiDeclineFriendRequest","apiAcceptFriendRequest","apiCancelFriendRequest"]),Object(s.b)("profile/dialogs",["createDialogWithUser","apiLoadAllDialogs"]),Object(s.b)("users/info",["apiInfo"]),{blockedUser:function(){this.blocked||(this.modalText="Вы уверены, что хотите заблокировать пользователя "+this.info.fullName+"?",this.modalShow=!0,this.modalType="block")},profileAction:function(){var e=this;if(!this.blocked)return"FRIEND"===this.friend?(this.modalText="Вы уверены, что хотите удалить пользователя "+this.info.fullName+" из друзей?",this.modalShow=!0,void(this.modalType="deleteFriend")):"REQUEST_RECEIVED"===this.friend?(this.modalText="Добавить "+this.info.fullName+" в друзья?",this.modalShow=!0,void(this.modalType="requestReceived")):void("REQUEST_SENT"!==this.friend?this.apiAddFriends(this.info.id).then(function(){e.apiInfo(e.$route.params.id)}):this.apiCancelFriendRequest(this.info.id).then(function(){e.apiInfo(e.$route.params.id)}));this.apiUnblockUser(this.$route.params.id).then(function(){e.apiInfo(e.$route.params.id)})},closeModal:function(){this.modalShow=!1},onConfirm:function(){var e=this;"block"!==this.modalType?"deleteFriend"!==this.modalType?"requestReceived"!==this.modalType||this.apiAcceptFriendRequest(this.info.id).then(function(){e.apiInfo(e.$route.params.id),e.closeModal()}):this.apiDeleteFriends(this.$route.params.id).then(function(){e.apiInfo(e.$route.params.id),e.closeModal()}):this.apiBlockUser(this.$route.params.id).then(function(){e.apiInfo(e.$route.params.id),e.closeModal()})},onDeclineFriendRequest:function(){var e=this;this.apiDeclineFriendRequest(this.$route.params.id).then(function(){e.apiInfo(e.$route.params.id),e.closeModal()})},onSentMessage:function(){if(this.blocked)return!1;this.$router.push({name:"Im",query:{userId:this.info.id}})},ageToStr:function(e){var t=e%100;return e+" "+(t>=5&&t<=20?"лет":1==(t%=10)?"год":t>=2&&t<=4?"года":"лет")}})}),l={render:function(){var e=this,t=e.$createElement,i=e._self._c||t;return e.info?i("div",{staticClass:"profile-info"},[i("div",{staticClass:"profile-info__pic"},[i("div",{staticClass:"profile-info__img",class:{offline:!e.online&&!e.me}},[e.info.photo?i("img",{attrs:{src:e.info.photo,alt:e.info.fullName}}):i("img",{attrs:{src:"/static/img/user/2.webp",alt:e.info.fullName}})]),e.me||!e.meBlocked||e.blocked?!e.me&&e.meBlocked&&e.blocked?i("div",{staticClass:"profile-info__actions"},[i("button-hover",{attrs:{disable:e.blocked},nativeOn:{click:function(t){return e.onSentMessage(t)}}},[e._v("Вы заблокированы")]),i("button-hover",{staticClass:"profile-info__add",attrs:{variant:e.btnVariantInfo.variant,bordered:"bordered"},nativeOn:{click:function(t){return e.profileAction(t)}}},[e._v(e._s(e.btnVariantInfo.text))])],1):e.me?e._e():i("div",{staticClass:"profile-info__actions"},[i("button-hover",{attrs:{disable:e.blocked},nativeOn:{click:function(t){return e.onSentMessage(t)}}},[e._v("Написать сообщение")]),i("button-hover",{staticClass:"profile-info__add",attrs:{variant:e.btnVariantInfo.variant,bordered:"bordered"},nativeOn:{click:function(t){return e.profileAction(t)}}},[e._v(e._s(e.btnVariantInfo.text))])],1):i("div",{staticClass:"profile-info__actions"},[i("button-hover",{attrs:{disable:"disable"}},[e._v("Вы заблокированы")])],1)]),i("div",{staticClass:"profile-info__main"},[e.me?i("router-link",{directives:[{name:"tooltip",rawName:"v-tooltip.bottom",value:"Редактировать профиль",expression:"'Редактировать профиль'",modifiers:{bottom:!0}}],staticClass:"edit",attrs:{to:{name:"Settings"}}},[i("simple-svg",{attrs:{filepath:"/static/img/edit.svg"}})],1):i("span",{staticClass:"profile-info__blocked",class:{blocked:e.blocked},on:{click:e.blockedUser}},[e._v(e._s(e.blockedText))]),i("div",{staticClass:"profile-info__header"},[i("h1",{staticClass:"profile-info__name"},[e._v(e._s(e.info.fullName))]),i("span",{staticClass:"user-status",class:{online:e.online,offline:!e.online}},[e._v(e._s(e.statusText))])]),i("div",{staticClass:"profile-info__block"},[i("span",{staticClass:"profile-info__title"},[e._v("Дата рождения:")]),e.info.birth_date?i("span",{staticClass:"profile-info__val"},[e._v(e._s(e._f("moment")(e.info.birth_date,"D MMMM YYYY"))+" ("+e._s(e.ageToStr(e.info.ages))+")")]):i("span",{staticClass:"profile-info__val"},[e._v("не заполнено")])]),i("div",{staticClass:"profile-info__block"},[i("span",{staticClass:"profile-info__title"},[e._v("Телефон:")]),e.info.phone?i("a",{staticClass:"profile-info__val",attrs:{href:"tel:"+e.info.phone}},[e._v(e._s(e._f("phone")(e.info.phone)))]):i("a",{staticClass:"profile-info__val"},[e._v("не заполнено")])]),i("div",{staticClass:"profile-info__block"},[i("span",{staticClass:"profile-info__title"},[e._v("Страна, город:")]),e.info.country?i("span",{staticClass:"profile-info__val"},[e._v(e._s(e.info.country)+", "+e._s(e.info.city))]):i("span",{staticClass:"profile-info__val"},[e._v("не заполнено")])]),i("div",{staticClass:"profile-info__block"},[i("span",{staticClass:"profile-info__title"},[e._v("О себе:")]),e.info.about?i("span",{staticClass:"profile-info__val"},[e._v(e._s(e.info.about))]):i("span",{staticClass:"profile-info__val"},[e._v("не заполнено")])])],1),i("modal",{model:{value:e.modalShow,callback:function(t){e.modalShow=t},expression:"modalShow"}},[e.modalText?i("p",[e._v(e._s(e.modalText))]):e._e(),i("template",{slot:"actions"},["requestReceived"!=e.modalType?i("button-hover",{nativeOn:{click:function(t){return t.preventDefault(),e.onConfirm(t)}}},[e._v("Да")]):i("button-hover",{nativeOn:{click:function(t){return t.preventDefault(),e.onConfirm(t)}}},[e._v("Принять")]),"requestReceived"!=e.modalType?i("button-hover",{attrs:{variant:"red",bordered:"bordered"},nativeOn:{click:function(t){return e.closeModal(t)}}},[e._v("Отмена")]):i("button-hover",{attrs:{variant:"red",bordered:"bordered"},nativeOn:{click:function(t){return e.onDeclineFriendRequest(t)}}},[e._v("Отклонить")])],1)],2)],1):e._e()},staticRenderFns:[]};var f=i("VU/8")(r,l,!1,function(e){i("dPCo")},null,null);t.a=f.exports},xX9V:function(e,t,i){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var o=i("Dd8w"),n=i.n(o),s=i("UBpT"),a=i("tJ7h"),r=i("0Hd5"),l=i("NYxO"),f={name:"ProfileId",components:{FriendsPossible:s.a,ProfileInfo:a.a,NewsBlock:r.a},data:function(){return{loading:!1}},computed:n()({},Object(l.c)("users/info",["getUsersInfo","getWall"])),methods:n()({},Object(l.b)("users/info",["userInfoId"])),created:function(){this.userInfoId(this.$route.params.id)}},d={render:function(){var e=this,t=e.$createElement,i=e._self._c||t;return e.getUsersInfo?i("div",{staticClass:"profile inner-page"},[i("div",{staticClass:"inner-page__main"},[i("div",{staticClass:"profile__info"},[i("profile-info",{attrs:{info:e.getUsersInfo,me:e.getUsersInfo.me,meBlocked:e.getUsersInfo.is_you_blocked,blocked:e.getUsersInfo.is_blocked,friend:e.getUsersInfo.is_friend,online:e.getUsersInfo.is_onlined}})],1),i("div",{staticClass:"profile__news"},[i("div",{staticClass:"profile__tabs"},[i("span",{staticClass:"profile__tab active"},[e._v("Публикации "+e._s(e.getUsersInfo.first_name)+" ("+e._s(e.getWall.length)+")")])]),i("div",{staticClass:"profile__news-list"},e._l(e.getWall,function(e){return i("news-block",{key:e.id,attrs:{info:e}})}),1)])]),i("div",{staticClass:"inner-page__aside"},[i("friends-possible")],1)]):e._e()},staticRenderFns:[]},c=i("VU/8")(f,d,!1,null,null,null);t.default=c.exports}});
//# sourceMappingURL=7.203ec988edeae222ba83.js.map