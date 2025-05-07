<template>
  <div id="app">
    <nav>
      <router-link to="/register">註冊</router-link> |
      <router-link to="/login">登入</router-link> |
      <router-link to="/posts">發文</router-link>
    </nav>
    <router-view/>
  </div>
</template>

<style>
#app {
  font-family: 'Microsoft JhengHei', 'PMingLiU', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
  text-decoration: none;
  margin: 0 10px;
}

nav a.router-link-exact-active {
  color: #42b983;
}
</style> 