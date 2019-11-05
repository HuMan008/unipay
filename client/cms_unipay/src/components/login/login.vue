<template>
  <div class="bg">
    <div class="loginBox">
      <h2 class="color_main">支付管理平台</h2>
      <a-form
        id="components-form-demo-normal-login"
        :form="form"
        class="login-form"
        @submit="handleSubmit"
      >
        <a-form-item>
          <a-input
            v-decorator="[
              'code',
              { rules: [{ required: true, message: '请输入用户名!' }] }
            ]"
            placeholder="请输入用户名"
          >
            <a-icon slot="prefix" type="user" style="color: rgba(0,0,0,.25)" />
          </a-input>
        </a-form-item>
        <a-form-item>
          <a-input
            v-decorator="[
              'pwd',
              { rules: [{ required: true, message: '请输入密码' }] }
            ]"
            type="password"
            placeholder="请输入密码"
          >
            <a-icon slot="prefix" type="lock" style="color: rgba(0,0,0,.25)" />
          </a-input>
        </a-form-item>
        <a-form-item>
          <a-button
            type="primary"
            size="large"
            block
            html-type="submit"
            class="login-form-button"
          >登 录</a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>
<script>
import { MemberService } from "../../services/MemberService.js";
// import { store } from '../../store'
export default {
  name: "Login",
  metaInfo() {
    return {
      title: "支付管理平台：登录"
    };
  },
  data() {
    return {
      // user: '',
      // password: ''
    };
  },
  beforeCreate() {
    this.form = this.$form.createForm(this);
  },
  mounted() {},
  methods: {
    handleSubmit(e) {
      e.preventDefault();
      // console.log(this.$message)
      this.form.validateFields((err, values) => {
        // console.log('Received values of form: ', values)

        if (!err) {
          // const postData = {
          //   'phone': values.userName,
          //   'code': '8888',
          //   'osuid': '2',
          //   'merid': 'ZH00070',
          //   'appid': '1'
          // }
          MemberService.login(values).then(res => {
            const r = res.data;
            // console.log(r);
            if (r.status === 0) {
              this.$message.success("登录成功");
              let user = {
                token: r.data.token,
                nickName: r.data.code,
                roleSet: r.data.roles,
                permissionSet: r.data.permissions
              };
              this.$store.commit("SET_USER", user);
              setTimeout(() => {
                this.$router.push("/main/Dashboards");
              }, 2000);
            } else {
              this.$message.error(r.message);
            }
          });
        }
      });
    }
  }
};
</script>
<style scoped>
.loginBox {
  width: 350px;
  background-color: rgba(255, 255, 255, 0.7);
  padding: 30px 20px;
  /* border-radius: 5px; */
  position: absolute;
  left: 50%;
  top: 40%;
  transform: translate(-50%, -50%);
}

.bg {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background-image: url("../../assets/img/login_bg.jpg");
  background-size: cover;
}

h2 {
  text-align: center;
}
</style>
