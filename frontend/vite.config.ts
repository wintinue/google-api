import path from "node:path";
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  build: {
    rollupOptions: {
      input: {
        home: path.resolve(__dirname, "index.html"),
        donggeurami: path.resolve(__dirname, "apps/donggeurami/index.html"),
        testyMarket: path.resolve(__dirname, "apps/testy-market/index.html")
      }
    }
  }
});
