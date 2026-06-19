import { CommonModule } from '@angular/common';
import { Component, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import * as i0 from "@angular/core";
import * as i1 from "./spring-guardian-api.service";
import * as i2 from "@angular/common";
import * as i3 from "@angular/forms";
function AppComponent_div_32_small_4_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "small");
    i0.ɵɵtext(1);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    let tmp_3_0;
    const ctx_r2 = i0.ɵɵnextContext(2);
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate2("", ctx_r2.t("selected"), ": ", (tmp_3_0 = ctx_r2.selectedFile()) == null ? null : tmp_3_0.name, "");
} }
function AppComponent_div_32_small_5_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "small");
    i0.ɵɵtext(1);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const ctx_r2 = i0.ɵɵnextContext(2);
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.t("zipHelp"));
} }
function AppComponent_div_32_Template(rf, ctx) { if (rf & 1) {
    const _r2 = i0.ɵɵgetCurrentView();
    i0.ɵɵelementStart(0, "div", 24)(1, "label", 25);
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(3, "input", 26);
    i0.ɵɵlistener("change", function AppComponent_div_32_Template_input_change_3_listener($event) { i0.ɵɵrestoreView(_r2); const ctx_r2 = i0.ɵɵnextContext(); return i0.ɵɵresetView(ctx_r2.onFileSelected($event)); });
    i0.ɵɵelementEnd();
    i0.ɵɵtemplate(4, AppComponent_div_32_small_4_Template, 2, 2, "small", 27)(5, AppComponent_div_32_small_5_Template, 2, 1, "small", 27);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const ctx_r2 = i0.ɵɵnextContext();
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("zipProjectFile"));
    i0.ɵɵadvance(2);
    i0.ɵɵproperty("ngIf", ctx_r2.selectedFile());
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", !ctx_r2.selectedFile());
} }
function AppComponent_div_33_small_4_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "small");
    i0.ɵɵtext(1);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const ctx_r2 = i0.ɵɵnextContext(2);
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate2("", ctx_r2.t("selectedFemale"), ": ", ctx_r2.folderFilesLabel(), "");
} }
function AppComponent_div_33_small_5_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "small");
    i0.ɵɵtext(1);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const ctx_r2 = i0.ɵɵnextContext(2);
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.t("folderHelp"));
} }
function AppComponent_div_33_Template(rf, ctx) { if (rf & 1) {
    const _r4 = i0.ɵɵgetCurrentView();
    i0.ɵɵelementStart(0, "div", 24)(1, "label", 28);
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(3, "input", 29);
    i0.ɵɵlistener("change", function AppComponent_div_33_Template_input_change_3_listener($event) { i0.ɵɵrestoreView(_r4); const ctx_r2 = i0.ɵɵnextContext(); return i0.ɵɵresetView(ctx_r2.onFolderSelected($event)); });
    i0.ɵɵelementEnd();
    i0.ɵɵtemplate(4, AppComponent_div_33_small_4_Template, 2, 2, "small", 27)(5, AppComponent_div_33_small_5_Template, 2, 1, "small", 27);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const ctx_r2 = i0.ɵɵnextContext();
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("projectRootFolder"));
    i0.ɵɵadvance(2);
    i0.ɵɵproperty("ngIf", ctx_r2.selectedFolderFiles().length > 0);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", ctx_r2.selectedFolderFiles().length === 0);
} }
function AppComponent_div_34_Template(rf, ctx) { if (rf & 1) {
    const _r5 = i0.ɵɵgetCurrentView();
    i0.ɵɵelementStart(0, "div", 24)(1, "label", 30);
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(3, "input", 31);
    i0.ɵɵtwoWayListener("ngModelChange", function AppComponent_div_34_Template_input_ngModelChange_3_listener($event) { i0.ɵɵrestoreView(_r5); const ctx_r2 = i0.ɵɵnextContext(); i0.ɵɵtwoWayBindingSet(ctx_r2.localPath, $event) || (ctx_r2.localPath = $event); return i0.ɵɵresetView($event); });
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(4, "small");
    i0.ɵɵtext(5);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const ctx_r2 = i0.ɵɵnextContext();
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("backendFolderPath"));
    i0.ɵɵadvance();
    i0.ɵɵtwoWayProperty("ngModel", ctx_r2.localPath);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("backendPathHelp"));
} }
function AppComponent_p_75_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "p", 32);
    i0.ɵɵtext(1);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const ctx_r2 = i0.ɵɵnextContext();
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.error());
} }
function AppComponent_main_76_article_9_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "article", 37)(1, "span");
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(3, "strong");
    i0.ɵɵtext(4);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(5, "p");
    i0.ɵɵtext(6);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const severity_r7 = ctx.$implicit;
    const ctx_r2 = i0.ɵɵnextContext(2);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.severityLabel(severity_r7));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.severityCount(severity_r7));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("findings"));
} }
function AppComponent_main_76_p_65_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "p");
    i0.ɵɵtext(1);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const current_r8 = i0.ɵɵnextContext().ngIf;
    const ctx_r2 = i0.ɵɵnextContext();
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.shortPath(current_r8.projectRootPath));
} }
function AppComponent_main_76_p_66_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "p");
    i0.ɵɵtext(1);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const ctx_r2 = i0.ɵɵnextContext(2);
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.shortPath(ctx_r2.currentScanSource()));
} }
function AppComponent_main_76_div_74_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "div", 54)(1, "div", 55)(2, "span");
    i0.ɵɵtext(3);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(4, "strong");
    i0.ɵɵtext(5);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(6, "div", 56);
    i0.ɵɵelement(7, "i");
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const severity_r9 = ctx.$implicit;
    const current_r8 = i0.ɵɵnextContext().ngIf;
    const ctx_r2 = i0.ɵɵnextContext();
    i0.ɵɵattribute("data-severity", severity_r9);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.severityLabel(severity_r9));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", ctx_r2.severityCount(severity_r9), " \u00B7 ", ctx_r2.severityPercent(current_r8, severity_r9), "%");
    i0.ɵɵadvance(2);
    i0.ɵɵstyleProp("width", ctx_r2.severityPercent(current_r8, severity_r9), "%");
} }
function AppComponent_main_76_article_83_Template(rf, ctx) { if (rf & 1) {
    const _r10 = i0.ɵɵgetCurrentView();
    i0.ɵɵelementStart(0, "article", 57)(1, "span");
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(3, "strong");
    i0.ɵɵtext(4);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(5, "p");
    i0.ɵɵtext(6);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(7, "button", 58);
    i0.ɵɵlistener("click", function AppComponent_main_76_article_83_Template_button_click_7_listener() { const lane_r11 = i0.ɵɵrestoreView(_r10).$implicit; const ctx_r2 = i0.ɵɵnextContext(2); return i0.ɵɵresetView(ctx_r2.focusDecisionLane(lane_r11.lane)); });
    i0.ɵɵtext(8);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const lane_r11 = ctx.$implicit;
    const ctx_r2 = i0.ɵɵnextContext(2);
    i0.ɵɵattribute("data-lane", lane_r11.lane);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(lane_r11.title);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(lane_r11.count);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(lane_r11.text);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("openLane"));
} }
function AppComponent_main_76_section_103_p_13_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "p");
    i0.ɵɵtext(1);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const ctx_r2 = i0.ɵɵnextContext(3);
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.t("noBlockers"));
} }
function AppComponent_main_76_section_103_li_15_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "li");
    i0.ɵɵtext(1);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const blocker_r12 = ctx.$implicit;
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(blocker_r12);
} }
function AppComponent_main_76_section_103_p_19_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "p");
    i0.ɵɵtext(1);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const ctx_r2 = i0.ɵɵnextContext(3);
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.t("noWarnings"));
} }
function AppComponent_main_76_section_103_li_21_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "li");
    i0.ɵɵtext(1);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const warning_r13 = ctx.$implicit;
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(warning_r13);
} }
function AppComponent_main_76_section_103_span_32_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "span");
    i0.ɵɵtext(1);
    i0.ɵɵelementStart(2, "strong");
    i0.ɵɵtext(3);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const ctx_r2 = i0.ɵɵnextContext(3);
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate1("", ctx_r2.t("requestedSource"), ": ");
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.currentScanSource());
} }
function AppComponent_main_76_section_103_span_33_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "span");
    i0.ɵɵtext(1);
    i0.ɵɵelementStart(2, "strong");
    i0.ɵɵtext(3);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const current_r8 = i0.ɵɵnextContext(2).ngIf;
    const ctx_r2 = i0.ɵɵnextContext();
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate1("", ctx_r2.t("scannedRootPath"), ": ");
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(current_r8.projectRootPath);
} }
function AppComponent_main_76_section_103_article_47_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "article", 70)(1, "div", 71)(2, "strong");
    i0.ɵɵtext(3);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(4, "span");
    i0.ɵɵtext(5);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(6, "p");
    i0.ɵɵtext(7);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(8, "div", 72)(9, "span");
    i0.ɵɵtext(10);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(11, "span");
    i0.ɵɵtext(12);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(13, "span");
    i0.ɵɵtext(14);
    i0.ɵɵelementEnd()()();
} if (rf & 2) {
    const type_r14 = ctx.$implicit;
    const ctx_r2 = i0.ɵɵnextContext(3);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(type_r14.category);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", type_r14.findings, " ", ctx_r2.t("findings"), "");
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(type_r14.explanation);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate2("", type_r14.criticalFindings, " ", ctx_r2.t("critical").toLowerCase(), "");
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", type_r14.majorFindings, " ", ctx_r2.t("major").toLowerCase(), "");
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", type_r14.minorFindings, " ", ctx_r2.t("minor").toLowerCase(), "");
} }
function AppComponent_main_76_section_103_span_52_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "span");
    i0.ɵɵtext(1);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const item_r15 = ctx.$implicit;
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(item_r15);
} }
function AppComponent_main_76_section_103_li_65_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "li");
    i0.ɵɵtext(1);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const step_r16 = ctx.$implicit;
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(step_r16);
} }
function AppComponent_main_76_section_103_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "section", 59)(1, "article", 60)(2, "div", 61)(3, "h2");
    i0.ɵɵtext(4);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(5, "span");
    i0.ɵɵtext(6);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(7, "p");
    i0.ɵɵtext(8);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(9, "div", 62)(10, "div")(11, "h3");
    i0.ɵɵtext(12);
    i0.ɵɵelementEnd();
    i0.ɵɵtemplate(13, AppComponent_main_76_section_103_p_13_Template, 2, 1, "p", 27);
    i0.ɵɵelementStart(14, "ul");
    i0.ɵɵtemplate(15, AppComponent_main_76_section_103_li_15_Template, 2, 1, "li", 63);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(16, "div")(17, "h3");
    i0.ɵɵtext(18);
    i0.ɵɵelementEnd();
    i0.ɵɵtemplate(19, AppComponent_main_76_section_103_p_19_Template, 2, 1, "p", 27);
    i0.ɵɵelementStart(20, "ul");
    i0.ɵɵtemplate(21, AppComponent_main_76_section_103_li_21_Template, 2, 1, "li", 63);
    i0.ɵɵelementEnd()()()();
    i0.ɵɵelementStart(22, "article", 64)(23, "h2");
    i0.ɵɵtext(24);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(25, "p");
    i0.ɵɵtext(26);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(27, "div", 65)(28, "span");
    i0.ɵɵtext(29);
    i0.ɵɵelementStart(30, "strong");
    i0.ɵɵtext(31);
    i0.ɵɵelementEnd()();
    i0.ɵɵtemplate(32, AppComponent_main_76_section_103_span_32_Template, 4, 2, "span", 27)(33, AppComponent_main_76_section_103_span_33_Template, 4, 2, "span", 27);
    i0.ɵɵelementStart(34, "span");
    i0.ɵɵtext(35);
    i0.ɵɵelementStart(36, "strong");
    i0.ɵɵtext(37);
    i0.ɵɵpipe(38, "date");
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(39, "span");
    i0.ɵɵtext(40);
    i0.ɵɵelementStart(41, "strong");
    i0.ɵɵtext(42);
    i0.ɵɵelementEnd()()()();
    i0.ɵɵelementStart(43, "article", 64)(44, "h2");
    i0.ɵɵtext(45);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(46, "div", 66);
    i0.ɵɵtemplate(47, AppComponent_main_76_section_103_article_47_Template, 15, 10, "article", 67);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(48, "article", 68)(49, "h2");
    i0.ɵɵtext(50);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(51, "div", 69);
    i0.ɵɵtemplate(52, AppComponent_main_76_section_103_span_52_Template, 2, 1, "span", 63);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(53, "p");
    i0.ɵɵtext(54);
    i0.ɵɵelementStart(55, "strong");
    i0.ɵɵtext(56);
    i0.ɵɵelementEnd()()();
    i0.ɵɵelementStart(57, "article", 68)(58, "h2");
    i0.ɵɵtext(59);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(60, "p");
    i0.ɵɵtext(61);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(62, "p");
    i0.ɵɵtext(63);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(64, "ul");
    i0.ɵɵtemplate(65, AppComponent_main_76_section_103_li_65_Template, 2, 1, "li", 63);
    i0.ɵɵelementEnd()()();
} if (rf & 2) {
    const current_r8 = i0.ɵɵnextContext().ngIf;
    const ctx_r2 = i0.ɵɵnextContext();
    i0.ɵɵadvance();
    i0.ɵɵattribute("data-status", current_r8.releaseReadiness.status);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("releaseReadiness"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(current_r8.releaseReadiness.label);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(current_r8.releaseReadiness.explanation);
    i0.ɵɵadvance(4);
    i0.ɵɵtextInterpolate(ctx_r2.t("blockers"));
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", current_r8.releaseReadiness.blockers.length === 0);
    i0.ɵɵadvance(2);
    i0.ɵɵproperty("ngForOf", current_r8.releaseReadiness.blockers);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("warnings"));
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", current_r8.releaseReadiness.warnings.length === 0);
    i0.ɵɵadvance(2);
    i0.ɵɵproperty("ngForOf", current_r8.releaseReadiness.warnings);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("executiveSummary"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(current_r8.summary.executiveSummary);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate1("", ctx_r2.t("project"), ": ");
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(current_r8.projectName);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", ctx_r2.currentScanSource());
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", current_r8.projectRootPath);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate1("", ctx_r2.t("scan"), ": ");
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(i0.ɵɵpipeBind2(38, 31, current_r8.scannedAt, "medium"));
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate1("", ctx_r2.t("selectedProfile"), ": ");
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", ctx_r2.detectedScopeLabel(current_r8), " \u00B7 ", ctx_r2.architectureStyleLabel(current_r8.profile.architectureStyle), "");
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("findingType"));
    i0.ɵɵadvance(2);
    i0.ɵɵproperty("ngForOf", current_r8.findingsByType);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("detectedStack"));
    i0.ɵɵadvance(2);
    i0.ɵɵproperty("ngForOf", ctx_r2.capabilityItems(current_r8));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate1("", ctx_r2.t("detectedStyles"), ": ");
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(current_r8.capabilities.detectedArchitecturalStyles.join(", "));
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("howToRead"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(current_r8.explanation.scoreMeaning);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(current_r8.explanation.severityMeaning);
    i0.ɵɵadvance(2);
    i0.ɵɵproperty("ngForOf", current_r8.explanation.nextSteps);
} }
function AppComponent_main_76_section_104_article_11_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "article", 76)(1, "div", 71)(2, "strong");
    i0.ɵɵtext(3);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(4, "span");
    i0.ɵɵtext(5);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(6, "p");
    i0.ɵɵtext(7);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(8, "small");
    i0.ɵɵtext(9);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const module_r17 = ctx.$implicit;
    i0.ɵɵattribute("data-active", module_r17.active);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(module_r17.name);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(module_r17.status);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(module_r17.description);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(module_r17.evidence);
} }
function AppComponent_main_76_section_104_article_18_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "article", 77)(1, "div", 71)(2, "strong");
    i0.ɵɵtext(3);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(4, "span");
    i0.ɵɵtext(5);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(6, "p");
    i0.ɵɵtext(7);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(8, "strong");
    i0.ɵɵtext(9);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const finding_r18 = ctx.$implicit;
    const ctx_r2 = i0.ɵɵnextContext(3);
    i0.ɵɵproperty("hidden", finding_r18.findingType !== "SPRING_CAPABILITY_GAP");
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(finding_r18.title);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", ctx_r2.t("confidence"), ": ", ctx_r2.confidenceLabel(finding_r18), "");
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(finding_r18.guidance.riskImpact);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(finding_r18.guidance.recommendedApproach);
} }
function AppComponent_main_76_section_104_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "section", 59)(1, "article", 64)(2, "div", 61)(3, "div")(4, "h2");
    i0.ɵɵtext(5);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(6, "p");
    i0.ɵɵtext(7);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(8, "span");
    i0.ɵɵtext(9);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(10, "div", 73);
    i0.ɵɵtemplate(11, AppComponent_main_76_section_104_article_11_Template, 10, 5, "article", 74);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(12, "article", 64)(13, "h2");
    i0.ɵɵtext(14);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(15, "p");
    i0.ɵɵtext(16);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(17, "div", 66);
    i0.ɵɵtemplate(18, AppComponent_main_76_section_104_article_18_Template, 10, 6, "article", 75);
    i0.ɵɵelementEnd()()();
} if (rf & 2) {
    const current_r8 = i0.ɵɵnextContext().ngIf;
    const ctx_r2 = i0.ɵɵnextContext();
    i0.ɵɵadvance(5);
    i0.ɵɵtextInterpolate(ctx_r2.t("springModules"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("springModulesSubtitle"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", ctx_r2.moduleCards().length, " ", ctx_r2.t("springModules").toLowerCase(), "");
    i0.ɵɵadvance(2);
    i0.ɵɵproperty("ngForOf", ctx_r2.moduleCards());
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate2("", ctx_r2.t("springModules"), " \u00B7 ", ctx_r2.t("springAdvisor"), "");
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("advisorSubtitle"));
    i0.ɵɵadvance(2);
    i0.ɵɵproperty("ngForOf", current_r8.findings);
} }
function AppComponent_main_76_section_105_article_8_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "article", 80)(1, "div", 71)(2, "strong");
    i0.ɵɵtext(3);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(4, "span");
    i0.ɵɵtext(5);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(6, "p");
    i0.ɵɵtext(7);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(8, "small");
    i0.ɵɵtext(9);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const gate_r19 = ctx.$implicit;
    const ctx_r2 = i0.ɵɵnextContext(3);
    i0.ɵɵattribute("data-status", gate_r19.status);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(gate_r19.name);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.gateStatusLabel(gate_r19.status));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(gate_r19.explanation);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", gate_r19.failingFindings, " ", ctx_r2.t("failingFindings"), "");
} }
function AppComponent_main_76_section_105_article_13_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "article", 70)(1, "div", 71)(2, "strong");
    i0.ɵɵtext(3);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(4, "span");
    i0.ɵɵtext(5);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(6, "p");
    i0.ɵɵtext(7);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(8, "div", 72)(9, "span");
    i0.ɵɵtext(10);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(11, "span");
    i0.ɵɵtext(12);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(13, "span");
    i0.ɵɵtext(14);
    i0.ɵɵelementEnd()()();
} if (rf & 2) {
    const area_r20 = ctx.$implicit;
    const ctx_r2 = i0.ɵɵnextContext(3);
    i0.ɵɵattribute("data-status", area_r20.readinessStatus);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(area_r20.name);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.gateStatusLabel(area_r20.readinessStatus));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(area_r20.description);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate2("", area_r20.findings, " ", ctx_r2.t("findings"), "");
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", area_r20.criticalFindings, " ", ctx_r2.t("critical").toLowerCase(), "");
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", area_r20.majorFindings, " ", ctx_r2.t("major").toLowerCase(), "");
} }
function AppComponent_main_76_section_105_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "section", 59)(1, "article", 64)(2, "div", 61)(3, "h2");
    i0.ɵɵtext(4);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(5, "span");
    i0.ɵɵtext(6);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(7, "div", 78);
    i0.ɵɵtemplate(8, AppComponent_main_76_section_105_article_8_Template, 10, 6, "article", 79);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(9, "article", 64)(10, "h2");
    i0.ɵɵtext(11);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(12, "div", 66);
    i0.ɵɵtemplate(13, AppComponent_main_76_section_105_article_13_Template, 15, 10, "article", 67);
    i0.ɵɵelementEnd()()();
} if (rf & 2) {
    const current_r8 = i0.ɵɵnextContext().ngIf;
    const ctx_r2 = i0.ɵɵnextContext();
    i0.ɵɵadvance(4);
    i0.ɵɵtextInterpolate(ctx_r2.t("gateStatus"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", current_r8.qualityGates.length, " ", ctx_r2.t("gates").toLowerCase(), "");
    i0.ɵɵadvance(2);
    i0.ɵɵproperty("ngForOf", current_r8.qualityGates);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("impactedAreas"));
    i0.ɵɵadvance(2);
    i0.ɵɵproperty("ngForOf", current_r8.architectureAreas);
} }
function AppComponent_main_76_section_106_option_6_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "option", 92);
    i0.ɵɵtext(1);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const severity_r22 = ctx.$implicit;
    const ctx_r2 = i0.ɵɵnextContext(3);
    i0.ɵɵproperty("value", severity_r22);
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.severityLabel(severity_r22));
} }
function AppComponent_main_76_section_106_option_10_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "option", 92);
    i0.ɵɵtext(1);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const category_r23 = ctx.$implicit;
    i0.ɵɵproperty("value", category_r23);
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(category_r23);
} }
function AppComponent_main_76_section_106_option_14_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "option", 92);
    i0.ɵɵtext(1);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const type_r24 = ctx.$implicit;
    const ctx_r2 = i0.ɵɵnextContext(3);
    i0.ɵɵproperty("value", type_r24);
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.findingTypeLabel(type_r24));
} }
function AppComponent_main_76_section_106_div_17_button_3_Template(rf, ctx) { if (rf & 1) {
    const _r25 = i0.ɵɵgetCurrentView();
    i0.ɵɵelementStart(0, "button", 96);
    i0.ɵɵlistener("click", function AppComponent_main_76_section_106_div_17_button_3_Template_button_click_0_listener() { const type_r26 = i0.ɵɵrestoreView(_r25).$implicit; const ctx_r2 = i0.ɵɵnextContext(4); return i0.ɵɵresetView(ctx_r2.setTypeFilter(type_r26.type)); });
    i0.ɵɵelementStart(1, "span");
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(3, "strong");
    i0.ɵɵtext(4);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const type_r26 = ctx.$implicit;
    const ctx_r2 = i0.ɵɵnextContext(4);
    i0.ɵɵclassProp("active", ctx_r2.typeFilter === type_r26.type);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(type_r26.label);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(type_r26.occurrences);
} }
function AppComponent_main_76_section_106_div_17_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "div", 93)(1, "div", 94);
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd();
    i0.ɵɵtemplate(3, AppComponent_main_76_section_106_div_17_button_3_Template, 5, 4, "button", 95);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const ctx_r2 = i0.ɵɵnextContext(3);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("problemAreasTitle"));
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngForOf", ctx_r2.problemTypeSummaries());
} }
function AppComponent_main_76_section_106_div_18_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "div", 97);
    i0.ɵɵtext(1);
    i0.ɵɵelementStart(2, "strong");
    i0.ɵɵtext(3);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const ctx_r2 = i0.ɵɵnextContext(3);
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate1(" ", ctx_r2.t("focusedLane"), ": ");
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.severityFilter === "ALL" ? ctx_r2.activeDecisionLane() : ctx_r2.severityLabel(ctx_r2.severityFilter));
} }
function AppComponent_main_76_section_106_section_20_article_7_a_29_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "a", 115);
    i0.ɵɵtext(1);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const finding_r27 = i0.ɵɵnextContext().$implicit;
    const ctx_r2 = i0.ɵɵnextContext(4);
    i0.ɵɵproperty("href", finding_r27.guidance.documentationUrl, i0.ɵɵsanitizeUrl);
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate1(" ", ctx_r2.t("officialDocs"), " ");
} }
function AppComponent_main_76_section_106_section_20_article_7_div_30_div_1_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "div")(1, "h4");
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(3, "pre", 117)(4, "code");
    i0.ɵɵtext(5);
    i0.ɵɵelementEnd()()();
} if (rf & 2) {
    const finding_r27 = i0.ɵɵnextContext(2).$implicit;
    const ctx_r2 = i0.ɵɵnextContext(4);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("currentFinding"));
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(finding_r27.guidance.beforeExample);
} }
function AppComponent_main_76_section_106_section_20_article_7_div_30_div_2_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "div")(1, "h4");
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(3, "pre", 117)(4, "code");
    i0.ɵɵtext(5);
    i0.ɵɵelementEnd()()();
} if (rf & 2) {
    const finding_r27 = i0.ɵɵnextContext(2).$implicit;
    const ctx_r2 = i0.ɵɵnextContext(4);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("expectedImplementation"));
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(finding_r27.guidance.afterExample);
} }
function AppComponent_main_76_section_106_section_20_article_7_div_30_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "div", 116);
    i0.ɵɵtemplate(1, AppComponent_main_76_section_106_section_20_article_7_div_30_div_1_Template, 6, 2, "div", 27)(2, AppComponent_main_76_section_106_section_20_article_7_div_30_div_2_Template, 6, 2, "div", 27);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const finding_r27 = i0.ɵɵnextContext().$implicit;
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", finding_r27.guidance.beforeExample);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", finding_r27.guidance.afterExample);
} }
function AppComponent_main_76_section_106_section_20_article_7_p_34_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "p", 118);
    i0.ɵɵtext(1);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const ctx_r2 = i0.ɵɵnextContext(5);
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.t("showingFirstComponents"));
} }
function AppComponent_main_76_section_106_section_20_article_7_div_36_ng_container_7_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementContainerStart(0);
    i0.ɵɵtext(1);
    i0.ɵɵelementContainerEnd();
} if (rf & 2) {
    const component_r28 = i0.ɵɵnextContext().$implicit;
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate1(":", component_r28.line, "");
} }
function AppComponent_main_76_section_106_section_20_article_7_div_36_p_8_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "p")(1, "b");
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd();
    i0.ɵɵtext(3);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const component_r28 = i0.ɵɵnextContext().$implicit;
    const ctx_r2 = i0.ɵɵnextContext(5);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate1("", ctx_r2.t("technicalEvidence"), ":");
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate1(" ", component_r28.evidence, "");
} }
function AppComponent_main_76_section_106_section_20_article_7_div_36_pre_9_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "pre", 117)(1, "code");
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const component_r28 = i0.ɵɵnextContext().$implicit;
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(component_r28.codeSnippet);
} }
function AppComponent_main_76_section_106_section_20_article_7_div_36_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "div", 119)(1, "strong");
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(3, "span");
    i0.ɵɵtext(4);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(5, "span");
    i0.ɵɵtext(6);
    i0.ɵɵtemplate(7, AppComponent_main_76_section_106_section_20_article_7_div_36_ng_container_7_Template, 2, 1, "ng-container", 27);
    i0.ɵɵelementEnd();
    i0.ɵɵtemplate(8, AppComponent_main_76_section_106_section_20_article_7_div_36_p_8_Template, 4, 2, "p", 27)(9, AppComponent_main_76_section_106_section_20_article_7_div_36_pre_9_Template, 3, 1, "pre", 120);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const component_r28 = ctx.$implicit;
    const ctx_r2 = i0.ɵɵnextContext(5);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.componentTitle(component_r28));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.componentTypeLabel(component_r28.type));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(component_r28.filePath);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", component_r28.line);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", component_r28.evidence);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", component_r28.codeSnippet);
} }
function AppComponent_main_76_section_106_section_20_article_7_p_37_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "p", 118);
    i0.ɵɵtext(1);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const finding_r27 = i0.ɵɵnextContext().$implicit;
    const ctx_r2 = i0.ɵɵnextContext(4);
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate2("+", ctx_r2.remainingComponents(finding_r27), " ", ctx_r2.t("moreComponents"), "");
} }
function AppComponent_main_76_section_106_section_20_article_7_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "article", 101)(1, "div", 102)(2, "div")(3, "span", 103);
    i0.ɵɵtext(4);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(5, "span", 104);
    i0.ɵɵtext(6);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(7, "span", 105);
    i0.ɵɵtext(8);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(9, "strong");
    i0.ɵɵtext(10);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(11, "h3");
    i0.ɵɵtext(12);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(13, "div", 106)(14, "div", 107)(15, "span");
    i0.ɵɵtext(16);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(17, "p");
    i0.ɵɵtext(18);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(19, "div", 108)(20, "span");
    i0.ɵɵtext(21);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(22, "p");
    i0.ɵɵtext(23);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(24, "div", 109)(25, "span");
    i0.ɵɵtext(26);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(27, "p");
    i0.ɵɵtext(28);
    i0.ɵɵelementEnd()()();
    i0.ɵɵtemplate(29, AppComponent_main_76_section_106_section_20_article_7_a_29_Template, 2, 2, "a", 110)(30, AppComponent_main_76_section_106_section_20_article_7_div_30_Template, 3, 2, "div", 111);
    i0.ɵɵelementStart(31, "details")(32, "summary");
    i0.ɵɵtext(33);
    i0.ɵɵelementEnd();
    i0.ɵɵtemplate(34, AppComponent_main_76_section_106_section_20_article_7_p_34_Template, 2, 1, "p", 112);
    i0.ɵɵelementStart(35, "div", 113);
    i0.ɵɵtemplate(36, AppComponent_main_76_section_106_section_20_article_7_div_36_Template, 10, 6, "div", 114);
    i0.ɵɵelementEnd();
    i0.ɵɵtemplate(37, AppComponent_main_76_section_106_section_20_article_7_p_37_Template, 2, 2, "p", 112);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const finding_r27 = ctx.$implicit;
    const ctx_r2 = i0.ɵɵnextContext(4);
    i0.ɵɵadvance(3);
    i0.ɵɵattribute("data-severity", finding_r27.severity);
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.severityLabel(finding_r27.severity));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(finding_r27.category);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", ctx_r2.t("technicalCode"), " ", ctx_r2.ruleCode(finding_r27.ruleId), "");
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.occurrenceLabel(finding_r27.occurrences));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(finding_r27.title);
    i0.ɵɵadvance(4);
    i0.ɵɵtextInterpolate(ctx_r2.t("detectedProblem"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(finding_r27.guidance.detectedProblem);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("riskImpact"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(finding_r27.guidance.riskImpact);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("recommendedFix"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(finding_r27.guidance.recommendedApproach);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", finding_r27.guidance.documentationUrl);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", false);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate2("", ctx_r2.t("involvedComponents"), " (", finding_r27.affectedComponents.length, ")");
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", finding_r27.affectedComponents.length > 12);
    i0.ɵɵadvance(2);
    i0.ɵɵproperty("ngForOf", ctx_r2.visibleComponents(finding_r27));
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", ctx_r2.remainingComponents(finding_r27) > 0);
} }
function AppComponent_main_76_section_106_section_20_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "section", 98)(1, "div", 99)(2, "div")(3, "span");
    i0.ɵɵtext(4);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(5, "h3");
    i0.ɵɵtext(6);
    i0.ɵɵelementEnd()()();
    i0.ɵɵtemplate(7, AppComponent_main_76_section_106_section_20_article_7_Template, 38, 20, "article", 100);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const group_r29 = ctx.$implicit;
    const ctx_r2 = i0.ɵɵnextContext(3);
    i0.ɵɵadvance(4);
    i0.ɵɵtextInterpolate(group_r29.label);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", group_r29.occurrences, " ", ctx_r2.t("findings"), "");
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngForOf", group_r29.findings)("ngForTrackBy", ctx_r2.trackFinding);
} }
function AppComponent_main_76_section_106_Template(rf, ctx) { if (rf & 1) {
    const _r21 = i0.ɵɵgetCurrentView();
    i0.ɵɵelementStart(0, "section", 68)(1, "div", 81)(2, "input", 82);
    i0.ɵɵtwoWayListener("ngModelChange", function AppComponent_main_76_section_106_Template_input_ngModelChange_2_listener($event) { i0.ɵɵrestoreView(_r21); const ctx_r2 = i0.ɵɵnextContext(2); i0.ɵɵtwoWayBindingSet(ctx_r2.search, $event) || (ctx_r2.search = $event); return i0.ɵɵresetView($event); });
    i0.ɵɵlistener("ngModelChange", function AppComponent_main_76_section_106_Template_input_ngModelChange_2_listener() { i0.ɵɵrestoreView(_r21); const ctx_r2 = i0.ɵɵnextContext(2); return i0.ɵɵresetView(ctx_r2.touchFilters()); });
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(3, "select", 83);
    i0.ɵɵtwoWayListener("ngModelChange", function AppComponent_main_76_section_106_Template_select_ngModelChange_3_listener($event) { i0.ɵɵrestoreView(_r21); const ctx_r2 = i0.ɵɵnextContext(2); i0.ɵɵtwoWayBindingSet(ctx_r2.severityFilter, $event) || (ctx_r2.severityFilter = $event); return i0.ɵɵresetView($event); });
    i0.ɵɵlistener("ngModelChange", function AppComponent_main_76_section_106_Template_select_ngModelChange_3_listener() { i0.ɵɵrestoreView(_r21); const ctx_r2 = i0.ɵɵnextContext(2); return i0.ɵɵresetView(ctx_r2.touchFilters()); });
    i0.ɵɵelementStart(4, "option", 84);
    i0.ɵɵtext(5);
    i0.ɵɵelementEnd();
    i0.ɵɵtemplate(6, AppComponent_main_76_section_106_option_6_Template, 2, 2, "option", 85);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(7, "select", 86);
    i0.ɵɵtwoWayListener("ngModelChange", function AppComponent_main_76_section_106_Template_select_ngModelChange_7_listener($event) { i0.ɵɵrestoreView(_r21); const ctx_r2 = i0.ɵɵnextContext(2); i0.ɵɵtwoWayBindingSet(ctx_r2.categoryFilter, $event) || (ctx_r2.categoryFilter = $event); return i0.ɵɵresetView($event); });
    i0.ɵɵlistener("ngModelChange", function AppComponent_main_76_section_106_Template_select_ngModelChange_7_listener() { i0.ɵɵrestoreView(_r21); const ctx_r2 = i0.ɵɵnextContext(2); return i0.ɵɵresetView(ctx_r2.touchFilters()); });
    i0.ɵɵelementStart(8, "option", 84);
    i0.ɵɵtext(9);
    i0.ɵɵelementEnd();
    i0.ɵɵtemplate(10, AppComponent_main_76_section_106_option_10_Template, 2, 2, "option", 85);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(11, "select", 87);
    i0.ɵɵtwoWayListener("ngModelChange", function AppComponent_main_76_section_106_Template_select_ngModelChange_11_listener($event) { i0.ɵɵrestoreView(_r21); const ctx_r2 = i0.ɵɵnextContext(2); i0.ɵɵtwoWayBindingSet(ctx_r2.typeFilter, $event) || (ctx_r2.typeFilter = $event); return i0.ɵɵresetView($event); });
    i0.ɵɵlistener("ngModelChange", function AppComponent_main_76_section_106_Template_select_ngModelChange_11_listener() { i0.ɵɵrestoreView(_r21); const ctx_r2 = i0.ɵɵnextContext(2); return i0.ɵɵresetView(ctx_r2.touchFilters()); });
    i0.ɵɵelementStart(12, "option", 84);
    i0.ɵɵtext(13);
    i0.ɵɵelementEnd();
    i0.ɵɵtemplate(14, AppComponent_main_76_section_106_option_14_Template, 2, 2, "option", 85);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(15, "button", 58);
    i0.ɵɵlistener("click", function AppComponent_main_76_section_106_Template_button_click_15_listener() { i0.ɵɵrestoreView(_r21); const ctx_r2 = i0.ɵɵnextContext(2); return i0.ɵɵresetView(ctx_r2.resetFilters()); });
    i0.ɵɵtext(16);
    i0.ɵɵelementEnd()();
    i0.ɵɵtemplate(17, AppComponent_main_76_section_106_div_17_Template, 4, 2, "div", 88)(18, AppComponent_main_76_section_106_div_18_Template, 4, 2, "div", 89);
    i0.ɵɵelementStart(19, "div", 90);
    i0.ɵɵtemplate(20, AppComponent_main_76_section_106_section_20_Template, 8, 5, "section", 91);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const ctx_r2 = i0.ɵɵnextContext(2);
    i0.ɵɵadvance(2);
    i0.ɵɵtwoWayProperty("ngModel", ctx_r2.search);
    i0.ɵɵproperty("placeholder", ctx_r2.t("searchPlaceholder"));
    i0.ɵɵadvance();
    i0.ɵɵtwoWayProperty("ngModel", ctx_r2.severityFilter);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("allSeverities"));
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngForOf", ctx_r2.severityOrder);
    i0.ɵɵadvance();
    i0.ɵɵtwoWayProperty("ngModel", ctx_r2.categoryFilter);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("allAreas"));
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngForOf", ctx_r2.categories());
    i0.ɵɵadvance();
    i0.ɵɵtwoWayProperty("ngModel", ctx_r2.typeFilter);
    i0.ɵɵattribute("aria-label", ctx_r2.t("findingType"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("allTypes"));
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngForOf", ctx_r2.findingTypes());
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("clearFilters"));
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", ctx_r2.problemTypeSummaries().length > 0);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", ctx_r2.activeDecisionLane());
    i0.ɵɵadvance(2);
    i0.ɵɵproperty("ngForOf", ctx_r2.problemGroups());
} }
function AppComponent_main_76_section_107_div_9_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "div", 124)(1, "h2");
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(3, "p");
    i0.ɵɵtext(4);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const ctx_r2 = i0.ɵɵnextContext(3);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("advisorEmptyTitle"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("advisorEmptyText"));
} }
function AppComponent_main_76_section_107_div_10_section_1_article_6_a_35_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "a", 115);
    i0.ɵɵtext(1);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const finding_r30 = i0.ɵɵnextContext().$implicit;
    const ctx_r2 = i0.ɵɵnextContext(5);
    i0.ɵɵproperty("href", finding_r30.guidance.documentationUrl, i0.ɵɵsanitizeUrl);
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate1(" ", ctx_r2.t("officialDocs"), " ");
} }
function AppComponent_main_76_section_107_div_10_section_1_article_6_div_36_ng_container_7_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementContainerStart(0);
    i0.ɵɵtext(1);
    i0.ɵɵelementContainerEnd();
} if (rf & 2) {
    const component_r31 = i0.ɵɵnextContext().ngIf;
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate1(":", component_r31.line, "");
} }
function AppComponent_main_76_section_107_div_10_section_1_article_6_div_36_p_8_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "p")(1, "b");
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd();
    i0.ɵɵtext(3);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const component_r31 = i0.ɵɵnextContext().ngIf;
    const ctx_r2 = i0.ɵɵnextContext(6);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate1("", ctx_r2.t("technicalEvidence"), ":");
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate1(" ", component_r31.evidence, "");
} }
function AppComponent_main_76_section_107_div_10_section_1_article_6_div_36_pre_9_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "pre", 117)(1, "code");
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const component_r31 = i0.ɵɵnextContext().ngIf;
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(component_r31.codeSnippet);
} }
function AppComponent_main_76_section_107_div_10_section_1_article_6_div_36_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "div", 134)(1, "h4");
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(3, "p")(4, "strong");
    i0.ɵɵtext(5);
    i0.ɵɵelementEnd();
    i0.ɵɵtext(6);
    i0.ɵɵtemplate(7, AppComponent_main_76_section_107_div_10_section_1_article_6_div_36_ng_container_7_Template, 2, 1, "ng-container", 27);
    i0.ɵɵelementEnd();
    i0.ɵɵtemplate(8, AppComponent_main_76_section_107_div_10_section_1_article_6_div_36_p_8_Template, 4, 2, "p", 27)(9, AppComponent_main_76_section_107_div_10_section_1_article_6_div_36_pre_9_Template, 3, 1, "pre", 120);
    i0.ɵɵelementStart(10, "p", 118);
    i0.ɵɵtext(11);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const component_r31 = ctx.ngIf;
    const ctx_r2 = i0.ɵɵnextContext(6);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("realEvidence"));
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.componentTitle(component_r31));
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate1(" \u00B7 ", component_r31.filePath, "");
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", component_r31.line);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", component_r31.evidence);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", component_r31.codeSnippet);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("examplesHidden"));
} }
function AppComponent_main_76_section_107_div_10_section_1_article_6_div_37_div_1_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "div")(1, "h4");
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(3, "pre", 117)(4, "code");
    i0.ɵɵtext(5);
    i0.ɵɵelementEnd()()();
} if (rf & 2) {
    const finding_r30 = i0.ɵɵnextContext(2).$implicit;
    const ctx_r2 = i0.ɵɵnextContext(5);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("beforeExample"));
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(finding_r30.guidance.beforeExample);
} }
function AppComponent_main_76_section_107_div_10_section_1_article_6_div_37_div_2_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "div")(1, "h4");
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(3, "pre", 117)(4, "code");
    i0.ɵɵtext(5);
    i0.ɵɵelementEnd()()();
} if (rf & 2) {
    const finding_r30 = i0.ɵɵnextContext(2).$implicit;
    const ctx_r2 = i0.ɵɵnextContext(5);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("afterExample"));
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(finding_r30.guidance.afterExample);
} }
function AppComponent_main_76_section_107_div_10_section_1_article_6_div_37_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "div", 135);
    i0.ɵɵtemplate(1, AppComponent_main_76_section_107_div_10_section_1_article_6_div_37_div_1_Template, 6, 2, "div", 27)(2, AppComponent_main_76_section_107_div_10_section_1_article_6_div_37_div_2_Template, 6, 2, "div", 27);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const finding_r30 = i0.ɵɵnextContext().$implicit;
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", finding_r30.guidance.beforeExample);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", finding_r30.guidance.afterExample);
} }
function AppComponent_main_76_section_107_div_10_section_1_article_6_div_42_ng_container_7_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementContainerStart(0);
    i0.ɵɵtext(1);
    i0.ɵɵelementContainerEnd();
} if (rf & 2) {
    const component_r32 = i0.ɵɵnextContext().$implicit;
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate1(":", component_r32.line, "");
} }
function AppComponent_main_76_section_107_div_10_section_1_article_6_div_42_p_8_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "p")(1, "b");
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd();
    i0.ɵɵtext(3);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const component_r32 = i0.ɵɵnextContext().$implicit;
    const ctx_r2 = i0.ɵɵnextContext(6);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate1("", ctx_r2.t("technicalEvidence"), ":");
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate1(" ", component_r32.evidence, "");
} }
function AppComponent_main_76_section_107_div_10_section_1_article_6_div_42_pre_9_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "pre", 117)(1, "code");
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const component_r32 = i0.ɵɵnextContext().$implicit;
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(component_r32.codeSnippet);
} }
function AppComponent_main_76_section_107_div_10_section_1_article_6_div_42_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "div", 119)(1, "strong");
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(3, "span");
    i0.ɵɵtext(4);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(5, "span");
    i0.ɵɵtext(6);
    i0.ɵɵtemplate(7, AppComponent_main_76_section_107_div_10_section_1_article_6_div_42_ng_container_7_Template, 2, 1, "ng-container", 27);
    i0.ɵɵelementEnd();
    i0.ɵɵtemplate(8, AppComponent_main_76_section_107_div_10_section_1_article_6_div_42_p_8_Template, 4, 2, "p", 27)(9, AppComponent_main_76_section_107_div_10_section_1_article_6_div_42_pre_9_Template, 3, 1, "pre", 120);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const component_r32 = ctx.$implicit;
    const ctx_r2 = i0.ɵɵnextContext(6);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.componentTitle(component_r32));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.componentTypeLabel(component_r32.type));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(component_r32.filePath);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", component_r32.line);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", component_r32.evidence);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", component_r32.codeSnippet);
} }
function AppComponent_main_76_section_107_div_10_section_1_article_6_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "article", 130)(1, "div", 102)(2, "div")(3, "span", 103);
    i0.ɵɵtext(4);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(5, "span", 104);
    i0.ɵɵtext(6);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(7, "span", 105);
    i0.ɵɵtext(8);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(9, "strong");
    i0.ɵɵtext(10);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(11, "h3");
    i0.ɵɵtext(12);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(13, "div", 131)(14, "div")(15, "span");
    i0.ɵɵtext(16);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(17, "p");
    i0.ɵɵtext(18);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(19, "div")(20, "span");
    i0.ɵɵtext(21);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(22, "p")(23, "strong");
    i0.ɵɵtext(24);
    i0.ɵɵelementEnd()()();
    i0.ɵɵelementStart(25, "div")(26, "span");
    i0.ɵɵtext(27);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(28, "p");
    i0.ɵɵtext(29);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(30, "div")(31, "span");
    i0.ɵɵtext(32);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(33, "p");
    i0.ɵɵtext(34);
    i0.ɵɵelementEnd()()();
    i0.ɵɵtemplate(35, AppComponent_main_76_section_107_div_10_section_1_article_6_a_35_Template, 2, 2, "a", 110)(36, AppComponent_main_76_section_107_div_10_section_1_article_6_div_36_Template, 12, 7, "div", 132)(37, AppComponent_main_76_section_107_div_10_section_1_article_6_div_37_Template, 3, 2, "div", 133);
    i0.ɵɵelementStart(38, "details")(39, "summary");
    i0.ɵɵtext(40);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(41, "div", 113);
    i0.ɵɵtemplate(42, AppComponent_main_76_section_107_div_10_section_1_article_6_div_42_Template, 10, 6, "div", 114);
    i0.ɵɵelementEnd()()();
} if (rf & 2) {
    const finding_r30 = ctx.$implicit;
    const ctx_r2 = i0.ɵɵnextContext(5);
    i0.ɵɵadvance(3);
    i0.ɵɵattribute("data-severity", finding_r30.severity);
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.severityLabel(finding_r30.severity));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.advisorArea(finding_r30));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", ctx_r2.t("technicalCode"), " ", ctx_r2.ruleCode(finding_r30.ruleId), "");
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.occurrenceLabel(finding_r30.occurrences));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(finding_r30.title);
    i0.ɵɵadvance(4);
    i0.ɵɵtextInterpolate(ctx_r2.t("detectedProblem"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(finding_r30.guidance.detectedProblem);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("springAlternativeToUse"));
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(finding_r30.guidance.springAlternative);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("riskImpact"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(finding_r30.guidance.riskImpact);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("recommendedFix"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(finding_r30.guidance.recommendedApproach);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", finding_r30.guidance.documentationUrl);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", ctx_r2.firstComponent(finding_r30));
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", false);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate2("", ctx_r2.t("involvedComponents"), " (", finding_r30.affectedComponents.length, ")");
    i0.ɵɵadvance(2);
    i0.ɵɵproperty("ngForOf", finding_r30.affectedComponents);
} }
function AppComponent_main_76_section_107_div_10_section_1_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "section", 127)(1, "div", 128)(2, "h3");
    i0.ɵɵtext(3);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(4, "span");
    i0.ɵɵtext(5);
    i0.ɵɵelementEnd()();
    i0.ɵɵtemplate(6, AppComponent_main_76_section_107_div_10_section_1_article_6_Template, 43, 21, "article", 129);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const group_r33 = ctx.$implicit;
    const ctx_r2 = i0.ɵɵnextContext(4);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(group_r33.area);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", group_r33.occurrences, " ", ctx_r2.t("advisorOpportunities"), "");
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngForOf", group_r33.findings)("ngForTrackBy", ctx_r2.trackFinding);
} }
function AppComponent_main_76_section_107_div_10_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "div", 125);
    i0.ɵɵtemplate(1, AppComponent_main_76_section_107_div_10_section_1_Template, 7, 5, "section", 126);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const ctx_r2 = i0.ɵɵnextContext(3);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngForOf", ctx_r2.advisorGroups());
} }
function AppComponent_main_76_section_107_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "section", 121)(1, "div", 61)(2, "div")(3, "h2");
    i0.ɵɵtext(4);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(5, "p");
    i0.ɵɵtext(6);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(7, "span");
    i0.ɵɵtext(8);
    i0.ɵɵelementEnd()();
    i0.ɵɵtemplate(9, AppComponent_main_76_section_107_div_9_Template, 5, 2, "div", 122)(10, AppComponent_main_76_section_107_div_10_Template, 2, 1, "div", 123);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const current_r8 = i0.ɵɵnextContext().ngIf;
    const ctx_r2 = i0.ɵɵnextContext();
    i0.ɵɵadvance(4);
    i0.ɵɵtextInterpolate(ctx_r2.t("springAdvisor"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("advisorSubtitle"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", ctx_r2.advisorCount(current_r8), " ", ctx_r2.t("advisorOpportunities"), "");
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", ctx_r2.springAdvisorFindings().length === 0);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", ctx_r2.springAdvisorFindings().length > 0);
} }
function AppComponent_main_76_section_108_article_10_div_24_ng_container_7_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementContainerStart(0);
    i0.ɵɵtext(1);
    i0.ɵɵelementContainerEnd();
} if (rf & 2) {
    const component_r34 = i0.ɵɵnextContext().ngIf;
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate1(":", component_r34.line, "");
} }
function AppComponent_main_76_section_108_article_10_div_24_p_8_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "p")(1, "b");
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd();
    i0.ɵɵtext(3);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const component_r34 = i0.ɵɵnextContext().ngIf;
    const ctx_r2 = i0.ɵɵnextContext(4);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate1("", ctx_r2.t("technicalEvidence"), ":");
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate1(" ", component_r34.evidence, "");
} }
function AppComponent_main_76_section_108_article_10_div_24_pre_9_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "pre", 117)(1, "code");
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const component_r34 = i0.ɵɵnextContext().ngIf;
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(component_r34.codeSnippet);
} }
function AppComponent_main_76_section_108_article_10_div_24_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "div", 134)(1, "h4");
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(3, "p")(4, "strong");
    i0.ɵɵtext(5);
    i0.ɵɵelementEnd();
    i0.ɵɵtext(6);
    i0.ɵɵtemplate(7, AppComponent_main_76_section_108_article_10_div_24_ng_container_7_Template, 2, 1, "ng-container", 27);
    i0.ɵɵelementEnd();
    i0.ɵɵtemplate(8, AppComponent_main_76_section_108_article_10_div_24_p_8_Template, 4, 2, "p", 27)(9, AppComponent_main_76_section_108_article_10_div_24_pre_9_Template, 3, 1, "pre", 120);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const component_r34 = ctx.ngIf;
    const ctx_r2 = i0.ɵɵnextContext(4);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("realEvidence"));
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.componentTitle(component_r34));
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate1(" \u00B7 ", component_r34.filePath, "");
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", component_r34.line);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", component_r34.evidence);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", component_r34.codeSnippet);
} }
function AppComponent_main_76_section_108_article_10_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "article", 101)(1, "div", 102)(2, "div")(3, "span", 103);
    i0.ɵɵtext(4);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(5, "span", 104);
    i0.ɵɵtext(6);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(7, "span", 105);
    i0.ɵɵtext(8);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(9, "strong");
    i0.ɵɵtext(10);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(11, "h3");
    i0.ɵɵtext(12);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(13, "div", 136)(14, "div")(15, "span");
    i0.ɵɵtext(16);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(17, "p");
    i0.ɵɵtext(18);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(19, "div")(20, "span");
    i0.ɵɵtext(21);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(22, "p");
    i0.ɵɵtext(23);
    i0.ɵɵelementEnd()()();
    i0.ɵɵtemplate(24, AppComponent_main_76_section_108_article_10_div_24_Template, 10, 6, "div", 132);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const finding_r35 = ctx.$implicit;
    const ctx_r2 = i0.ɵɵnextContext(3);
    i0.ɵɵadvance(3);
    i0.ɵɵattribute("data-severity", finding_r35.severity);
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.severityLabel(finding_r35.severity));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(finding_r35.category);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", ctx_r2.t("technicalCode"), " ", ctx_r2.ruleCode(finding_r35.ruleId), "");
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.occurrenceLabel(finding_r35.occurrences));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(finding_r35.title);
    i0.ɵɵadvance(4);
    i0.ɵɵtextInterpolate(ctx_r2.t("riskImpact"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(finding_r35.guidance.riskImpact);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("recommendedFix"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(finding_r35.guidance.recommendedApproach);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", ctx_r2.firstComponent(finding_r35));
} }
function AppComponent_main_76_section_108_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "section", 121)(1, "div", 61)(2, "div")(3, "h2");
    i0.ɵɵtext(4);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(5, "p");
    i0.ɵɵtext(6);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(7, "span");
    i0.ɵɵtext(8);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(9, "div", 90);
    i0.ɵɵtemplate(10, AppComponent_main_76_section_108_article_10_Template, 25, 12, "article", 100);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const ctx_r2 = i0.ɵɵnextContext(2);
    i0.ɵɵadvance(4);
    i0.ɵɵtextInterpolate(ctx_r2.t("productionRules"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("productionRulesSubtitle"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", ctx_r2.productionFindings().length, " ", ctx_r2.t("findings"), "");
    i0.ɵɵadvance(2);
    i0.ɵɵproperty("ngForOf", ctx_r2.productionFindings())("ngForTrackBy", ctx_r2.trackFinding);
} }
function AppComponent_main_76_section_109_article_10_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "article", 101)(1, "div", 102)(2, "div")(3, "span", 103);
    i0.ɵɵtext(4);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(5, "span", 104);
    i0.ɵɵtext(6);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(7, "span", 105);
    i0.ɵɵtext(8);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(9, "strong");
    i0.ɵɵtext(10);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(11, "h3");
    i0.ɵɵtext(12);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(13, "p");
    i0.ɵɵtext(14);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(15, "strong");
    i0.ɵɵtext(16);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const finding_r36 = ctx.$implicit;
    const ctx_r2 = i0.ɵɵnextContext(3);
    i0.ɵɵadvance(3);
    i0.ɵɵattribute("data-severity", finding_r36.severity);
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.severityLabel(finding_r36.severity));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(finding_r36.category);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", ctx_r2.t("confidence"), ": ", ctx_r2.confidenceLabel(finding_r36), "");
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.occurrenceLabel(finding_r36.occurrences));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(finding_r36.title);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(finding_r36.whyItMatters);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(finding_r36.suggestedFix);
} }
function AppComponent_main_76_section_109_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "section", 121)(1, "div", 61)(2, "div")(3, "h2");
    i0.ɵɵtext(4);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(5, "p");
    i0.ɵɵtext(6);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(7, "span");
    i0.ɵɵtext(8);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(9, "div", 90);
    i0.ɵɵtemplate(10, AppComponent_main_76_section_109_article_10_Template, 17, 9, "article", 100);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const ctx_r2 = i0.ɵɵnextContext(2);
    i0.ɵɵadvance(4);
    i0.ɵɵtextInterpolate(ctx_r2.t("suggestionsToVerify"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("suggestionsSubtitle"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", ctx_r2.suggestionFindings().length, " ", ctx_r2.t("findings"), "");
    i0.ɵɵadvance(2);
    i0.ɵɵproperty("ngForOf", ctx_r2.suggestionFindings())("ngForTrackBy", ctx_r2.trackFinding);
} }
function AppComponent_main_76_section_110_article_7_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "article", 139)(1, "span", 140);
    i0.ɵɵtext(2);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(3, "div")(4, "div", 141)(5, "span", 103);
    i0.ɵɵtext(6);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(7, "span", 105);
    i0.ɵɵtext(8);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(9, "span");
    i0.ɵɵtext(10);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(11, "h3");
    i0.ɵɵtext(12);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(13, "p");
    i0.ɵɵtext(14);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(15, "strong");
    i0.ɵɵtext(16);
    i0.ɵɵelementEnd()()();
} if (rf & 2) {
    const action_r37 = ctx.$implicit;
    const ctx_r2 = i0.ɵɵnextContext(3);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate1("#", action_r37.priority, "");
    i0.ɵɵadvance(3);
    i0.ɵɵattribute("data-severity", action_r37.severity);
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.severityLabel(action_r37.severity));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.ruleCode(action_r37.ruleId));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(action_r37.location);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(action_r37.title);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(action_r37.reason);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(action_r37.action);
} }
function AppComponent_main_76_section_110_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "section", 68)(1, "div", 61)(2, "h2");
    i0.ɵɵtext(3);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(4, "span");
    i0.ɵɵtext(5);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(6, "div", 137);
    i0.ɵɵtemplate(7, AppComponent_main_76_section_110_article_7_Template, 17, 8, "article", 138);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const current_r8 = i0.ɵɵnextContext().ngIf;
    const ctx_r2 = i0.ɵɵnextContext();
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("recommendedActions"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", current_r8.recommendedActions.length, " ", ctx_r2.t("priorities"), "");
    i0.ɵɵadvance(2);
    i0.ɵɵproperty("ngForOf", current_r8.recommendedActions);
} }
function AppComponent_main_76_section_111_Template(rf, ctx) { if (rf & 1) {
    const _r38 = i0.ɵɵgetCurrentView();
    i0.ɵɵelementStart(0, "section", 68)(1, "div", 61)(2, "h2");
    i0.ɵɵtext(3);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(4, "button", 58);
    i0.ɵɵlistener("click", function AppComponent_main_76_section_111_Template_button_click_4_listener() { i0.ɵɵrestoreView(_r38); const ctx_r2 = i0.ɵɵnextContext(2); return i0.ɵɵresetView(ctx_r2.exportJson()); });
    i0.ɵɵtext(5);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(6, "p", 118);
    i0.ɵɵtext(7);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(8, "pre");
    i0.ɵɵtext(9);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const ctx_r2 = i0.ɵɵnextContext(2);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("technicalJson"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("exportJson"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("jsonNote"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.rawJson());
} }
function AppComponent_main_76_Template(rf, ctx) { if (rf & 1) {
    const _r6 = i0.ɵɵgetCurrentView();
    i0.ɵɵelementStart(0, "main")(1, "section", 33)(2, "article", 34)(3, "span");
    i0.ɵɵtext(4);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(5, "strong");
    i0.ɵɵtext(6);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(7, "p");
    i0.ɵɵtext(8);
    i0.ɵɵelementEnd()();
    i0.ɵɵtemplate(9, AppComponent_main_76_article_9_Template, 7, 3, "article", 35);
    i0.ɵɵelementStart(10, "article", 36)(11, "span");
    i0.ɵɵtext(12);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(13, "strong");
    i0.ɵɵtext(14);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(15, "p");
    i0.ɵɵtext(16);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(17, "article", 37)(18, "span");
    i0.ɵɵtext(19);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(20, "strong");
    i0.ɵɵtext(21);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(22, "p");
    i0.ɵɵtext(23);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(24, "article", 37)(25, "span");
    i0.ɵɵtext(26);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(27, "strong");
    i0.ɵɵtext(28);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(29, "p");
    i0.ɵɵtext(30);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(31, "article", 37)(32, "span");
    i0.ɵɵtext(33);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(34, "strong");
    i0.ɵɵtext(35);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(36, "p");
    i0.ɵɵtext(37);
    i0.ɵɵelementEnd()()();
    i0.ɵɵelementStart(38, "section", 38)(39, "article", 39)(40, "span");
    i0.ɵɵtext(41);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(42, "strong");
    i0.ɵɵtext(43);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(44, "p");
    i0.ɵɵtext(45);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(46, "article", 40)(47, "span");
    i0.ɵɵtext(48);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(49, "strong");
    i0.ɵɵtext(50);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(51, "p");
    i0.ɵɵtext(52);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(53, "article", 40)(54, "span");
    i0.ɵɵtext(55);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(56, "strong");
    i0.ɵɵtext(57);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(58, "p");
    i0.ɵɵtext(59);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(60, "article", 41)(61, "span");
    i0.ɵɵtext(62);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(63, "strong");
    i0.ɵɵtext(64);
    i0.ɵɵelementEnd();
    i0.ɵɵtemplate(65, AppComponent_main_76_p_65_Template, 2, 1, "p", 27)(66, AppComponent_main_76_p_66_Template, 2, 1, "p", 27);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(67, "section", 42)(68, "div", 43)(69, "h2");
    i0.ɵɵtext(70);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(71, "p");
    i0.ɵɵtext(72);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(73, "div", 44);
    i0.ɵɵtemplate(74, AppComponent_main_76_div_74_Template, 8, 6, "div", 45);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(75, "section", 46)(76, "div", 47)(77, "div")(78, "h2");
    i0.ɵɵtext(79);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(80, "p");
    i0.ɵɵtext(81);
    i0.ɵɵelementEnd()()();
    i0.ɵɵelementStart(82, "div", 48);
    i0.ɵɵtemplate(83, AppComponent_main_76_article_83_Template, 9, 5, "article", 49);
    i0.ɵɵelementEnd()();
    i0.ɵɵelementStart(84, "nav", 50)(85, "button", 12);
    i0.ɵɵlistener("click", function AppComponent_main_76_Template_button_click_85_listener() { i0.ɵɵrestoreView(_r6); const ctx_r2 = i0.ɵɵnextContext(); return i0.ɵɵresetView(ctx_r2.selectTab("overview")); });
    i0.ɵɵtext(86);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(87, "button", 12);
    i0.ɵɵlistener("click", function AppComponent_main_76_Template_button_click_87_listener() { i0.ɵɵrestoreView(_r6); const ctx_r2 = i0.ɵɵnextContext(); return i0.ɵɵresetView(ctx_r2.selectTab("modules")); });
    i0.ɵɵtext(88);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(89, "button", 12);
    i0.ɵɵlistener("click", function AppComponent_main_76_Template_button_click_89_listener() { i0.ɵɵrestoreView(_r6); const ctx_r2 = i0.ɵɵnextContext(); return i0.ɵɵresetView(ctx_r2.selectTab("gates")); });
    i0.ɵɵtext(90);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(91, "button", 12);
    i0.ɵɵlistener("click", function AppComponent_main_76_Template_button_click_91_listener() { i0.ɵɵrestoreView(_r6); const ctx_r2 = i0.ɵɵnextContext(); return i0.ɵɵresetView(ctx_r2.selectTab("findings")); });
    i0.ɵɵtext(92);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(93, "button", 12);
    i0.ɵɵlistener("click", function AppComponent_main_76_Template_button_click_93_listener() { i0.ɵɵrestoreView(_r6); const ctx_r2 = i0.ɵɵnextContext(); return i0.ɵɵresetView(ctx_r2.selectTab("advisor")); });
    i0.ɵɵtext(94);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(95, "button", 12);
    i0.ɵɵlistener("click", function AppComponent_main_76_Template_button_click_95_listener() { i0.ɵɵrestoreView(_r6); const ctx_r2 = i0.ɵɵnextContext(); return i0.ɵɵresetView(ctx_r2.selectTab("production")); });
    i0.ɵɵtext(96);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(97, "button", 12);
    i0.ɵɵlistener("click", function AppComponent_main_76_Template_button_click_97_listener() { i0.ɵɵrestoreView(_r6); const ctx_r2 = i0.ɵɵnextContext(); return i0.ɵɵresetView(ctx_r2.selectTab("suggestions")); });
    i0.ɵɵtext(98);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(99, "button", 12);
    i0.ɵɵlistener("click", function AppComponent_main_76_Template_button_click_99_listener() { i0.ɵɵrestoreView(_r6); const ctx_r2 = i0.ɵɵnextContext(); return i0.ɵɵresetView(ctx_r2.selectTab("actions")); });
    i0.ɵɵtext(100);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(101, "button", 12);
    i0.ɵɵlistener("click", function AppComponent_main_76_Template_button_click_101_listener() { i0.ɵɵrestoreView(_r6); const ctx_r2 = i0.ɵɵnextContext(); return i0.ɵɵresetView(ctx_r2.selectTab("json")); });
    i0.ɵɵtext(102);
    i0.ɵɵelementEnd()();
    i0.ɵɵtemplate(103, AppComponent_main_76_section_103_Template, 66, 34, "section", 51)(104, AppComponent_main_76_section_104_Template, 19, 9, "section", 51)(105, AppComponent_main_76_section_105_Template, 14, 6, "section", 51)(106, AppComponent_main_76_section_106_Template, 21, 16, "section", 52)(107, AppComponent_main_76_section_107_Template, 11, 6, "section", 53)(108, AppComponent_main_76_section_108_Template, 11, 6, "section", 53)(109, AppComponent_main_76_section_109_Template, 11, 6, "section", 53)(110, AppComponent_main_76_section_110_Template, 8, 4, "section", 52)(111, AppComponent_main_76_section_111_Template, 10, 4, "section", 52);
    i0.ɵɵelementEnd();
} if (rf & 2) {
    const current_r8 = ctx.ngIf;
    const ctx_r2 = i0.ɵɵnextContext();
    i0.ɵɵadvance(4);
    i0.ɵɵtextInterpolate(ctx_r2.t("architectureScore"));
    i0.ɵɵadvance();
    i0.ɵɵclassMap(ctx_r2.scoreClass(current_r8.architectureScore));
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(current_r8.architectureScore);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate2("", ctx_r2.riskLabel(current_r8.riskLevel), " \u00B7 ", current_r8.releaseReadiness.label, "");
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngForOf", ctx_r2.severityOrder);
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("springAdvisor"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.advisorCount(current_r8));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("advisorOpportunities"));
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("typeConfiguration"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.typeOccurrenceCount(current_r8, "CONFIGURATION"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("findings"));
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("typeDependencies"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.typeOccurrenceCount(current_r8, "DEPENDENCIES"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("findings"));
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("rulesExecuted"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(current_r8.rulesExecuted);
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate4("", current_r8.scannedJavaFiles, " ", ctx_r2.t("javaFiles"), " \u00B7 ", current_r8.scannedPomFiles, " ", ctx_r2.t("pomFiles"), "");
    i0.ɵɵadvance(4);
    i0.ɵɵtextInterpolate(ctx_r2.t("precisionMode"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("highConfidence"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("precisionModeText"));
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("scopeOnly"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.detectedScopeLabel(current_r8));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("scopeOnlyText"));
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("evidenceFirst"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate3("", current_r8.findings.length, " rules \u00B7 ", ctx_r2.totalFindingOccurrences(), " ", ctx_r2.t("occurrences"), "");
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("evidenceFirstText"));
    i0.ɵɵadvance(3);
    i0.ɵɵtextInterpolate(ctx_r2.t("projectIdentity"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(current_r8.projectName);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", current_r8.projectRootPath);
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", !current_r8.projectRootPath && ctx_r2.currentScanSource());
    i0.ɵɵadvance(4);
    i0.ɵɵtextInterpolate(ctx_r2.t("impactVisualTitle"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("impactVisualText"));
    i0.ɵɵadvance(2);
    i0.ɵɵproperty("ngForOf", ctx_r2.severityOrder);
    i0.ɵɵadvance(5);
    i0.ɵɵtextInterpolate(ctx_r2.t("decisionBoard"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("decisionBoardText"));
    i0.ɵɵadvance(2);
    i0.ɵɵproperty("ngForOf", ctx_r2.decisionLanes());
    i0.ɵɵadvance(2);
    i0.ɵɵclassProp("active", ctx_r2.activeTab() === "overview");
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.t("overview"));
    i0.ɵɵadvance();
    i0.ɵɵclassProp("active", ctx_r2.activeTab() === "modules");
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.t("springModules"));
    i0.ɵɵadvance();
    i0.ɵɵclassProp("active", ctx_r2.activeTab() === "gates");
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.t("gates"));
    i0.ɵɵadvance();
    i0.ɵɵclassProp("active", ctx_r2.activeTab() === "findings");
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.t("springArchitecture"));
    i0.ɵɵadvance();
    i0.ɵɵclassProp("active", ctx_r2.activeTab() === "advisor");
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.t("springAdvisor"));
    i0.ɵɵadvance();
    i0.ɵɵclassProp("active", ctx_r2.activeTab() === "production");
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.t("productionRules"));
    i0.ɵɵadvance();
    i0.ɵɵclassProp("active", ctx_r2.activeTab() === "suggestions");
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.t("suggestionsToVerify"));
    i0.ɵɵadvance();
    i0.ɵɵclassProp("active", ctx_r2.activeTab() === "actions");
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.t("actions"));
    i0.ɵɵadvance();
    i0.ɵɵclassProp("active", ctx_r2.activeTab() === "json");
    i0.ɵɵadvance();
    i0.ɵɵtextInterpolate(ctx_r2.t("technicalJson"));
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", ctx_r2.activeTab() === "overview");
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", ctx_r2.activeTab() === "modules");
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", ctx_r2.activeTab() === "gates");
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", ctx_r2.activeTab() === "findings");
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", ctx_r2.activeTab() === "advisor");
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", ctx_r2.activeTab() === "production");
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", ctx_r2.activeTab() === "suggestions");
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", ctx_r2.activeTab() === "actions");
    i0.ɵɵadvance();
    i0.ɵɵproperty("ngIf", ctx_r2.activeTab() === "json");
} }
function AppComponent_ng_template_77_Template(rf, ctx) { if (rf & 1) {
    i0.ɵɵelementStart(0, "section", 142);
    i0.ɵɵelement(1, "img", 143);
    i0.ɵɵelementStart(2, "h2");
    i0.ɵɵtext(3);
    i0.ɵɵelementEnd();
    i0.ɵɵelementStart(4, "p");
    i0.ɵɵtext(5);
    i0.ɵɵelementEnd()();
} if (rf & 2) {
    const ctx_r2 = i0.ɵɵnextContext();
    i0.ɵɵadvance();
    i0.ɵɵattribute("alt", ctx_r2.t("logoAlt"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("emptyTitle"));
    i0.ɵɵadvance(2);
    i0.ɵɵtextInterpolate(ctx_r2.t("emptyText"));
} }
const TRANSLATIONS = {
    it: {
        eyebrow: 'Scanner architetturale Spring',
        heroTitle: 'Proteggi la tua architettura Spring',
        brandSubtitle: 'Architettura · Prontezza · Modernizzazione',
        heroText: 'Analizza progetti Spring Boot, raggruppa i problemi per area tecnica e mostra in modo chiaro cosa correggere, perché conta e quali classi o file sono coinvolti.',
        precisionMode: 'Modalità precisa Web/Batch',
        precisionModeText: 'Regole più selettive, evidenza reale e nessun finding basato solo su import o esempi generici.',
        scopeOnly: 'Perimetro attivo',
        scopeOnlyText: 'Web/API e Spring Batch. Camel e altri profili restano fuori finché non avranno regole dedicate.',
        evidenceFirst: 'Evidenza prima di tutto',
        evidenceFirstText: 'Ogni card parte da file, riga e snippet realmente trovati nel progetto analizzato.',
        impactVisualTitle: 'Mappa impatto',
        impactVisualText: 'Distribuzione immediata delle severità per capire dove intervenire per primo.',
        projectIdentity: 'Identità scansione',
        highConfidence: 'Alta confidenza',
        language: 'Lingua report',
        italian: 'Italiano',
        english: 'Inglese',
        uploadZip: 'Carica ZIP',
        uploadFolder: 'Carica cartella',
        backendPath: 'Percorso backend',
        zipProjectFile: 'File ZIP del progetto',
        selected: 'Selezionato',
        selectedFemale: 'Selezionata',
        zipHelp: 'Usa questa modalità quando vuoi caricare un progetto compresso.',
        projectRootFolder: 'Cartella root del progetto',
        folderHelp: 'Seleziona la cartella principale del progetto, ad esempio quella che contiene il pom.xml.',
        backendFolderPath: 'Percorso cartella sul backend',
        backendPathHelp: 'Serve quando backend e progetto sono sulla stessa macchina o quando Docker monta una cartella sotto /scan.',
        scanSettings: 'Analisi automatica',
        projectType: 'Tipo rilevato',
        releaseTarget: 'Prontezza rilascio',
        knownIssues: 'Baseline legacy rilevata o dichiarata',
        profileHelp: 'Spring Guardian rileva automaticamente i moduli Spring dal POM e dal codice. Non devi scegliere il tipo progetto: carica il progetto e avvia la review.',
        automaticProfileTitle: 'Analisi automatica Spring',
        automaticProfileText: 'Carica il progetto: Spring Guardian rileva Web, Security, Batch, JPA, JDBC, Actuator, Validation e OpenAPI prima di applicare le regole.',
        analysisStepsTitle: 'Cosa succede dopo il click',
        analysisStepModules: '1. Rilevo i moduli Spring realmente presenti.',
        analysisStepRules: '2. Applico solo le regole compatibili con quei moduli.',
        analysisStepAlternatives: '3. Evidenzio alternative Spring concrete al codice manuale.',
        analysisStepProduction: '4. Controllo prontezza produzione, azioni e JSON tecnico.',
        noManualProfile: 'Nessun profilo manuale: Web, Batch e Production readiness vengono riconosciuti dalla scansione.',
        webApi: 'API Web / REST',
        batch: 'Batch',
        library: 'Libreria / starter',
        autoDetected: 'Rilevata automaticamente',
        production: 'Produzione',
        internal: 'Test / QA',
        legacyBaseline: 'Baseline legacy',
        scanRunning: 'Scansione in corso…',
        startScan: 'Avvia scansione',
        selectZipError: 'Seleziona un file ZIP da analizzare.',
        selectFolderError: 'Seleziona la cartella root del progetto da analizzare.',
        selectPathError: 'Inserisci il percorso di una cartella visibile dal backend.',
        scanError: 'Errore durante la scansione.',
        architectureScore: 'Punteggio architetturale',
        findings: 'problemi',
        rulesExecuted: 'Regole eseguite',
        javaFiles: 'file Java',
        pomFiles: 'POM',
        overview: 'Riepilogo',
        springModules: 'Moduli Spring',
        springModulesSubtitle: 'Capability rilevate dal POM e dal codice: il report applica le regole solo dove il modulo ha senso.',
        productionRules: 'Regole produzione',
        productionRulesSubtitle: 'Rischi di rilascio: configurazione, segreti, Actuator, Maven, profili e prontezza operativa.',
        suggestionsToVerify: 'Consigli da verificare',
        suggestionsSubtitle: 'Note a bassa confidenza o a basso impatto: utili per revisione, ma non trattarle come blocchi senza conferma.',
        capabilityDetected: 'Rilevato',
        capabilityMissing: 'Non rilevato',
        capabilityRecommended: 'Consigliato',
        confidence: 'Confidenza',
        highConfidenceLabel: 'Alta',
        mediumConfidenceLabel: 'Media',
        verifyConfidenceLabel: 'Da verificare',
        springArchitecture: 'Architettura Spring',
        gates: 'Controlli qualità',
        problems: 'Problemi',
        springAdvisor: 'Alternative Spring',
        advisorSubtitle: 'Uso manuale di API Java, componenti di basso livello e alternative Spring più integrate da valutare.',
        advisorOpportunities: 'suggerimenti',
        advisorEmptyTitle: 'Nessuna alternativa Spring rilevata',
        advisorEmptyText: 'Questa scansione non ha rilevato API Java manuali o componenti sostituibili con alternative Spring più integrate.',
        actions: 'Azioni',
        technicalJson: 'JSON tecnico',
        executiveSummary: 'Riepilogo esecutivo',
        releaseReadiness: 'Prontezza al rilascio',
        blockers: 'Blocchi',
        warnings: 'Avvisi',
        noBlockers: 'Nessun blocco rilevato per il profilo selezionato.',
        noWarnings: 'Nessuna avvertenza rilevata.',
        project: 'Progetto',
        scan: 'Scansione',
        scannedRootPath: 'Percorso analizzato',
        requestedSource: 'Sorgente richiesta',
        detectedStack: 'Stack rilevato',
        detectedStyles: 'Stile rilevato',
        selectedProfile: 'Rilevamento automatico',
        impactedAreas: 'Aree architetturali',
        howToRead: 'Come leggere il report',
        gateStatus: 'Stato gate',
        failingFindings: 'problemi bloccanti o rilevanti',
        searchPlaceholder: 'Cerca per codice, classe, file o testo...',
        allSeverities: 'Tutte le severità',
        allAreas: 'Tutte le aree',
        allTypes: 'Tutti i tipi',
        clearFilters: 'Rimuovi filtri',
        problemAreasTitle: 'Aree dei problemi',
        focusedLane: 'Filtro decisionale attivo',
        showingFirstComponents: 'Mostro le prime occorrenze rilevanti. Il JSON tecnico contiene l’elenco completo.',
        moreComponents: 'altre occorrenze nel JSON tecnico',
        currentFinding: 'Esempio generico non rilevato nel progetto',
        expectedImplementation: 'Esempio generico di soluzione',
        findingType: 'Tipo problema',
        typeCode: 'Codice Java',
        typePom: 'POM Maven',
        typeDependencies: 'Dipendenze',
        typeConfiguration: 'Configurazione',
        typeTest: 'Test',
        typeSecurity: 'Sicurezza',
        typeJpa: 'JPA e persistenza',
        typeWebLayer: 'Layer web/API',
        typeDependencyInjection: 'Iniezione delle dipendenze',
        typeRuntimeCode: 'Codice runtime',
        typeSpringAlternative: 'Spring Alternative Advisor',
        typeSpringBatch: 'Spring Batch',
        typeArchitecture: 'Architettura e confini',
        typeCloudReadiness: 'Prontezza cloud',
        typeObservability: 'Osservabilità',
        technicalCode: 'Codice regola',
        whyItMatters: 'Perché conta',
        recommendedFix: 'Soluzione consigliata',
        detectedProblem: 'Cosa ho rilevato',
        riskImpact: 'Cosa può comportare',
        springAlternativeToUse: 'Alternativa Spring da usare',
        officialDocs: 'Documentazione ufficiale',
        beforeExample: 'Esempio generico prima',
        afterExample: 'Esempio generico dopo',
        currentCode: 'Codice realmente rilevato',
        realEvidence: 'Evidenza reale nel progetto',
        examplesHidden: 'Gli esempi generici non vengono mostrati nella card principale: usa file, riga e snippet reali per verificare il finding.',
        solutionPattern: 'Come dovrebbe essere fatto',
        advisorArea: 'Area advisor',
        involvedComponents: 'Classi e file coinvolti',
        technicalEvidence: 'Evidenza rilevata',
        recommendedActions: 'Azioni consigliate',
        priorities: 'priorità',
        exportJson: 'Esporta JSON',
        jsonNote: 'Questa sezione è pensata per sviluppatori e pipeline CI. Per una lettura funzionale usa Riepilogo, Controlli qualità, Problemi e Azioni.',
        emptyTitle: 'Nessuna scansione eseguita',
        emptyText: 'Carica uno ZIP, seleziona la cartella root del progetto oppure indica un percorso leggibile dal backend.',
        critical: 'Critico',
        major: 'Alto',
        minor: 'Medio',
        info: 'Info',
        riskHigh: 'Rischio alto',
        riskMedium: 'Rischio medio',
        riskLow: 'Rischio basso',
        riskHealthy: 'Buono',
        actionRequired: 'Intervento richiesto',
        improvementRequired: 'Miglioramento richiesto',
        noFindings: 'Nessun problema rilevato',
        reviewRecommended: 'Revisione consigliata',
        projectComponent: 'Progetto',
        javaClass: 'Classe Java',
        testClass: 'Classe di test',
        mavenPom: 'POM Maven',
        configFile: 'File configurazione',
        file: 'File',
        occurrence: 'occorrenza',
        occurrences: 'occorrenze',
        selectedFolderFallback: 'cartella selezionata',
        yes: 'Sì',
        no: 'No',
        pass: 'Ok',
        fail: 'Fallito',
        warning: 'Attenzione',
        ok: 'Ok',
        review: 'Revisione',
        blocked: 'Bloccato',
        attention: 'Attenzione',
        decisionBoard: 'Priorità di intervento',
        decisionBoardText: 'Il report è ordinato per decisione: prima i blocchi di rilascio, poi i problemi importanti, il debito tecnico, gli advisor Spring e infine le note informative.',
        blockersLane: 'Blocchi produzione',
        blockersLaneText: 'Critici: correggili prima di considerare il rilascio.',
        importantLane: 'Da correggere prima del rilascio',
        importantLaneText: 'Alti: rischi concreti per sicurezza, architettura o manutenzione.',
        improvementsLane: 'Debito tecnico rilevante',
        improvementsLaneText: 'Medi: miglioramenti da pianificare nel backlog tecnico.',
        advisorLane: 'Suggerimenti Spring',
        advisorLaneText: 'Advisor: alternative Spring e modernizzazione, non blocchi immediati.',
        informationLane: 'Note informative',
        informationLaneText: 'Info: opportunità e best practice a basso impatto.',
        openLane: 'Apri',
        logoAlt: 'Logo Spring Guardian'
    },
    en: {
        eyebrow: 'Spring architecture scanner',
        heroTitle: 'Protect your Spring architecture',
        brandSubtitle: 'Architecture · Readiness · Modernization',
        heroText: 'Analyze Spring Boot projects, group findings by technical area and clearly show what should be fixed, why it matters and which classes or files are involved.',
        precisionMode: 'Precise Web/Batch mode',
        precisionModeText: 'More selective rules, real evidence and no findings based only on imports or generic examples.',
        scopeOnly: 'Active scope',
        scopeOnlyText: 'Web/API and Spring Batch. Camel and other profiles stay out until dedicated rules are available.',
        evidenceFirst: 'Evidence first',
        evidenceFirstText: 'Every card starts from file, line and snippet actually found in the analyzed project.',
        impactVisualTitle: 'Impact map',
        impactVisualText: 'Immediate severity distribution to understand where to act first.',
        projectIdentity: 'Scan identity',
        highConfidence: 'High confidence',
        language: 'Report language',
        italian: 'Italian',
        english: 'English',
        uploadZip: 'Upload ZIP',
        uploadFolder: 'Upload folder',
        backendPath: 'Backend path',
        zipProjectFile: 'Project ZIP file',
        selected: 'Selected',
        selectedFemale: 'Selected',
        zipHelp: 'Use this mode when you want to upload a compressed project.',
        projectRootFolder: 'Project root folder',
        folderHelp: 'Select the project root folder, for example the one containing pom.xml.',
        backendFolderPath: 'Backend folder path',
        backendPathHelp: 'Use this when the backend and the project are on the same machine or when Docker mounts a folder under /scan.',
        scanSettings: 'Automatic analysis',
        projectType: 'Detected type',
        releaseTarget: 'Release readiness',
        knownIssues: 'This is a legacy project with known issues',
        profileHelp: 'Spring Guardian detects Spring modules automatically from the POM and source code. You do not need to choose a project type: load the project and start the review.',
        automaticProfileTitle: 'Automatic Spring analysis',
        automaticProfileText: 'Load the project: Spring Guardian detects Web, Security, Batch, JPA, JDBC, Actuator, Validation and OpenAPI before applying rules.',
        analysisStepsTitle: 'What happens after scan',
        analysisStepModules: '1. Detect the Spring modules actually present.',
        analysisStepRules: '2. Apply only rules that match those modules.',
        analysisStepAlternatives: '3. Highlight concrete Spring alternatives to manual code.',
        analysisStepProduction: '4. Check production readiness, actions and technical JSON.',
        noManualProfile: 'No manual profile: Web, Batch and production readiness are inferred from the scan.',
        webApi: 'Web / REST API',
        batch: 'Batch',
        library: 'Library / starter',
        autoDetected: 'Automatically detected',
        production: 'Production',
        internal: 'Test / QA',
        legacyBaseline: 'Legacy baseline',
        scanRunning: 'Scan running...',
        startScan: 'Start scan',
        selectZipError: 'Select a ZIP file to analyze.',
        selectFolderError: 'Select the project root folder to analyze.',
        selectPathError: 'Enter a folder path visible from the backend.',
        scanError: 'Error during scan.',
        architectureScore: 'Architecture score',
        findings: 'findings',
        rulesExecuted: 'Executed rules',
        javaFiles: 'Java files',
        pomFiles: 'POM files',
        overview: 'Overview',
        springModules: 'Spring Modules',
        springModulesSubtitle: 'Capabilities detected from the POM and code: the report applies rules only where the module makes sense.',
        productionRules: 'Production Rules',
        productionRulesSubtitle: 'Release risks: configuration, secrets, Actuator, Maven, profiles and operational readiness.',
        suggestionsToVerify: 'Suggestions to Verify',
        suggestionsSubtitle: 'Low-confidence or low-impact notes: useful for review, but do not treat them as blockers without confirmation.',
        capabilityDetected: 'Detected',
        capabilityMissing: 'Not detected',
        capabilityRecommended: 'Recommended',
        confidence: 'Confidence',
        highConfidenceLabel: 'High',
        mediumConfidenceLabel: 'Medium',
        verifyConfidenceLabel: 'To verify',
        springArchitecture: 'Spring Architecture',
        gates: 'Quality gates',
        problems: 'Findings',
        springAdvisor: 'Spring Alternatives',
        advisorSubtitle: 'Manual Java objects, low-level APIs and modern Spring alternatives worth evaluating.',
        advisorOpportunities: 'suggestions',
        advisorEmptyTitle: 'No Spring alternative detected',
        advisorEmptyText: 'This scan did not find manual Java objects or APIs that should be replaced by more integrated Spring alternatives.',
        actions: 'Actions',
        technicalJson: 'Technical JSON',
        executiveSummary: 'Executive summary',
        releaseReadiness: 'Release readiness',
        blockers: 'Blockers',
        warnings: 'Warnings',
        noBlockers: 'No blocker detected for the selected profile.',
        noWarnings: 'No warning detected.',
        project: 'Project',
        scan: 'Scan',
        scannedRootPath: 'Scanned root path',
        requestedSource: 'Requested source',
        detectedStack: 'Detected stack',
        detectedStyles: 'Detected style',
        selectedProfile: 'Automatic detection',
        impactedAreas: 'Architecture areas',
        howToRead: 'How to read the report',
        gateStatus: 'Gate status',
        failingFindings: 'blocking or relevant findings',
        searchPlaceholder: 'Search by code, class, file or text...',
        allSeverities: 'All severities',
        allAreas: 'All areas',
        allTypes: 'All types',
        clearFilters: 'Clear filters',
        problemAreasTitle: 'Finding areas',
        focusedLane: 'Active decision filter',
        showingFirstComponents: 'Showing the first relevant occurrences. The technical JSON contains the full list.',
        moreComponents: 'more occurrences in the technical JSON',
        currentFinding: 'Generic example not detected in the project',
        expectedImplementation: 'Generic recommended solution example',
        findingType: 'Finding type',
        typeCode: 'Java code',
        typePom: 'Maven POM',
        typeDependencies: 'Dependencies',
        typeConfiguration: 'Configuration',
        typeTest: 'Tests',
        typeSecurity: 'Security',
        typeJpa: 'JPA and persistence',
        typeWebLayer: 'Web/API layer',
        typeDependencyInjection: 'Dependency injection',
        typeRuntimeCode: 'Runtime code',
        typeSpringAlternative: 'Spring Alternative Advisor',
        typeSpringBatch: 'Spring Batch',
        typeArchitecture: 'Architecture and boundaries',
        typeCloudReadiness: 'Cloud readiness',
        typeObservability: 'Observability',
        technicalCode: 'Technical code',
        whyItMatters: 'Why it matters',
        recommendedFix: 'Recommended solution',
        detectedProblem: 'What was detected',
        riskImpact: 'Possible impact',
        springAlternativeToUse: 'Spring alternative to use',
        officialDocs: 'Official documentation',
        beforeExample: 'Generic before example',
        afterExample: 'Generic after example',
        currentCode: 'Actually detected code',
        realEvidence: 'Real evidence in the project',
        examplesHidden: 'Generic examples are not shown in the main card: use file, line and real snippets to verify the finding.',
        solutionPattern: 'Expected implementation',
        advisorArea: 'Advisor area',
        involvedComponents: 'Involved classes and files',
        technicalEvidence: 'Technical evidence',
        recommendedActions: 'Recommended actions',
        priorities: 'priorities',
        exportJson: 'Export JSON',
        jsonNote: 'This section is meant for developers and CI pipelines. Functional reading is available in Overview, Quality gates, Findings and Actions.',
        emptyTitle: 'No scan executed',
        emptyText: 'Upload a ZIP, select the project root folder or enter a backend-readable path.',
        critical: 'Critical',
        major: 'High',
        minor: 'Medium',
        info: 'Info',
        riskHigh: 'High risk',
        riskMedium: 'Medium risk',
        riskLow: 'Low risk',
        riskHealthy: 'Healthy',
        actionRequired: 'Action required',
        improvementRequired: 'Improvement required',
        noFindings: 'No findings detected',
        reviewRecommended: 'Review recommended',
        projectComponent: 'Project',
        javaClass: 'Java class',
        testClass: 'Test class',
        mavenPom: 'Maven POM',
        configFile: 'Configuration file',
        file: 'File',
        occurrence: 'occurrence',
        occurrences: 'occurrences',
        selectedFolderFallback: 'selected folder',
        yes: 'Yes',
        no: 'No',
        pass: 'Pass',
        fail: 'Fail',
        warning: 'Warning',
        ok: 'Ok',
        review: 'Review',
        blocked: 'Blocked',
        attention: 'Attention',
        decisionBoard: 'Fix priority board',
        decisionBoardText: 'The report is organized by decision impact: release blockers first, then important issues, technical debt, Spring advisors and informational notes.',
        blockersLane: 'Production blockers',
        blockersLaneText: 'Critical findings: fix these before considering the release.',
        importantLane: 'Fix before release',
        importantLaneText: 'High findings: concrete security, architecture or maintenance risks.',
        improvementsLane: 'Relevant technical debt',
        improvementsLaneText: 'Medium findings: plan these in the technical backlog.',
        advisorLane: 'Spring suggestions',
        advisorLaneText: 'Advisor findings: Spring alternatives and modernization, not immediate blockers.',
        informationLane: 'Informational notes',
        informationLaneText: 'Info findings: low-impact opportunities and best practices.',
        openLane: 'Open',
        logoAlt: 'Spring Guardian logo'
    }
};
export class AppComponent {
    constructor(api) {
        this.api = api;
        this.severityOrder = ['CRITICAL', 'MAJOR', 'MINOR', 'INFO'];
        this.report = signal(null);
        this.loading = signal(false);
        this.error = signal(null);
        this.selectedFile = signal(null);
        this.selectedFolderFiles = signal([]);
        this.selectedFolderName = signal(null);
        this.uploadMode = signal('zip');
        this.activeTab = signal('overview');
        this.activeDecisionLane = signal(null);
        this.selectedLanguage = signal('it');
        this.filterVersion = signal(0);
        this.currentScanSource = signal(null);
        this.scanSequence = 0;
        this.localPath = '';
        this.search = '';
        this.severityFilter = 'ALL';
        this.categoryFilter = 'ALL';
        this.typeFilter = 'ALL';
        this.categories = computed(() => {
            const current = this.report();
            if (!current) {
                return [];
            }
            return [...new Set(current.findings.map((finding) => finding.category))].sort();
        });
        this.findingTypes = computed(() => {
            const current = this.report();
            if (!current) {
                return [];
            }
            return [...new Set(current.findings.map((finding) => this.findingType(finding)))].sort((left, right) => this.findingTypeLabel(left).localeCompare(this.findingTypeLabel(right)));
        });
        this.springAdvisorFindings = computed(() => {
            const current = this.report();
            if (!current) {
                return [];
            }
            return current.findings.filter((finding) => this.isSpringAdvisorFinding(finding));
        });
        this.advisorGroups = computed(() => {
            const groups = new Map();
            for (const finding of this.springAdvisorFindings()) {
                const area = this.advisorArea(finding);
                groups.set(area, [...(groups.get(area) ?? []), finding]);
            }
            return Array.from(groups.entries())
                .map(([area, findings]) => ({ area, findings, occurrences: findings.reduce((total, finding) => total + finding.occurrences, 0) }))
                .sort((left, right) => left.area.localeCompare(right.area));
        });
        this.moduleCards = computed(() => {
            const current = this.report();
            if (!current) {
                return [];
            }
            const c = current.capabilities;
            const it = this.selectedLanguage() === 'it';
            return [
                this.moduleCard('Spring Web/API', c.usesSpringWeb, c.usesSpringWeb ? 'spring-boot-starter-web / @RestController' : it ? 'Nessun segnale Web MVC/WebFlux' : 'No Web MVC/WebFlux signal', c.usesSpringWeb ? it ? 'Regole Web/API attive: controller, DTO, validazione, error handling e OpenAPI.' : 'Web/API rules active: controllers, DTOs, validation, error handling and OpenAPI.' : it ? 'Le regole Web restano inattive se il modulo non è presente.' : 'Web rules stay inactive when the module is not present.'),
                this.moduleCard('Spring Security', c.usesSpringSecurity, c.usesSpringSecurity ? 'spring-boot-starter-security / SecurityFilterChain' : it ? 'Nessun segnale Spring Security' : 'No Spring Security signal', c.usesSpringSecurity ? it ? 'Regole Security attive: matcher, chain, CSRF, header e configurazione.' : 'Security rules active: matchers, chains, CSRF, headers and configuration.' : it ? 'Le regole Security non vengono applicate senza segnali reali.' : 'Security rules are not applied without real signals.'),
                this.moduleCard('Spring Batch', c.usesSpringBatch, c.usesSpringBatch ? 'spring-batch-core / @EnableBatchProcessing' : it ? 'Nessun segnale Spring Batch' : 'No Spring Batch signal', c.usesSpringBatch ? it ? 'Regole Batch attive: job, step, reader, writer, retry, skip e restartability.' : 'Batch rules active: jobs, steps, readers, writers, retry, skip and restartability.' : it ? 'Le regole Batch restano inattive se il progetto non è Batch.' : 'Batch rules stay inactive when the project is not Batch.'),
                this.moduleCard('Spring Data JPA', c.usesJpa, c.usesJpa ? 'spring-boot-starter-data-jpa / @Entity' : it ? 'Nessun segnale JPA' : 'No JPA signal', c.usesJpa ? it ? 'Regole persistenza abilitate dove applicabili.' : 'Persistence rules enabled where applicable.' : it ? 'I controlli JPA restano inattivi.' : 'JPA checks stay inactive.'),
                this.moduleCard('Actuator', c.usesActuator, c.usesActuator ? 'spring-boot-starter-actuator' : it ? 'Actuator non rilevato' : 'Actuator not detected', c.usesActuator ? it ? 'Health, info, metriche e readiness possono essere valutate.' : 'Health, info, metrics and readiness can be reviewed.' : it ? 'Consigliato per prontezza produzione e osservabilità.' : 'Recommended for production readiness and observability.'),
                this.moduleCard('Bean Validation', c.usesValidation, c.usesValidation ? 'spring-boot-starter-validation / @Valid' : it ? 'Validation non rilevata' : 'Validation not detected', c.usesValidation ? it ? 'La validazione degli input può essere valutata.' : 'Input validation can be reviewed.' : it ? 'Consigliata per DTO e request body Web/API.' : 'Recommended for Web/API DTOs and request bodies.'),
                this.moduleCard('OpenAPI', c.usesOpenApi, c.usesOpenApi ? 'springdoc-openapi / @Operation' : it ? 'OpenAPI non rilevato' : 'OpenAPI not detected', c.usesOpenApi ? it ? 'La documentazione API può essere valutata.' : 'API documentation can be reviewed.' : it ? 'Consigliato per API pubbliche o interne governate.' : 'Recommended for public or governed internal APIs.')
            ];
        });
        this.productionFindings = computed(() => {
            const current = this.report();
            if (!current) {
                return [];
            }
            return current.findings.filter((finding) => this.isProductionFinding(finding));
        });
        this.suggestionFindings = computed(() => {
            const current = this.report();
            if (!current) {
                return [];
            }
            return current.findings.filter((finding) => this.isSuggestionFinding(finding));
        });
        this.problemTypeSummaries = computed(() => {
            const current = this.report();
            if (!current) {
                return [];
            }
            const groups = new Map();
            for (const finding of current.findings.filter((item) => !this.isSpringAdvisorFinding(item))) {
                const type = this.findingType(finding);
                groups.set(type, [...(groups.get(type) ?? []), finding]);
            }
            return Array.from(groups.entries())
                .map(([type, findings]) => ({
                type,
                label: this.findingTypeLabel(type),
                findings: findings.length,
                occurrences: findings.reduce((total, finding) => total + finding.occurrences, 0)
            }))
                .sort((left, right) => right.occurrences - left.occurrences);
        });
        this.problemGroups = computed(() => {
            const groups = new Map();
            for (const finding of this.filteredFindings()) {
                if (this.isSpringAdvisorFinding(finding)) {
                    continue;
                }
                const type = this.findingType(finding);
                groups.set(type, [...(groups.get(type) ?? []), finding]);
            }
            return Array.from(groups.entries())
                .map(([type, findings]) => ({
                type,
                label: this.findingTypeLabel(type),
                occurrences: findings.reduce((total, finding) => total + finding.occurrences, 0),
                findings
            }))
                .sort((left, right) => this.problemTypeRank(left.type) - this.problemTypeRank(right.type) || left.label.localeCompare(right.label));
        });
        this.decisionLanes = computed(() => {
            const current = this.report();
            if (!current) {
                return [];
            }
            return [
                { lane: 'BLOCKERS', title: this.t('blockersLane'), text: this.t('blockersLaneText'), count: this.decisionCount(current, 'BLOCKERS') },
                { lane: 'IMPORTANT', title: this.t('importantLane'), text: this.t('importantLaneText'), count: this.decisionCount(current, 'IMPORTANT') },
                { lane: 'IMPROVEMENTS', title: this.t('improvementsLane'), text: this.t('improvementsLaneText'), count: this.decisionCount(current, 'IMPROVEMENTS') },
                { lane: 'ADVISOR', title: this.t('advisorLane'), text: this.t('advisorLaneText'), count: this.decisionCount(current, 'ADVISOR') },
                { lane: 'INFORMATION', title: this.t('informationLane'), text: this.t('informationLaneText'), count: this.decisionCount(current, 'INFORMATION') }
            ];
        });
        this.filteredFindings = computed(() => {
            const current = this.report();
            if (!current) {
                return [];
            }
            this.filterVersion();
            const term = this.search.trim().toLowerCase();
            return current.findings.filter((finding) => {
                const activeLane = this.activeDecisionLane();
                const advisor = this.isSpringAdvisorFinding(finding);
                const includeAdvisor = activeLane === 'ADVISOR' || this.activeTab() === 'advisor';
                if (advisor && !includeAdvisor) {
                    return false;
                }
                const matchesLane = activeLane === null || this.inDecisionLane(finding, activeLane);
                const matchesSeverity = this.severityFilter === 'ALL' || finding.severity === this.severityFilter;
                const matchesCategory = this.categoryFilter === 'ALL' || finding.category === this.categoryFilter;
                const matchesType = this.typeFilter === 'ALL' || this.findingType(finding) === this.typeFilter;
                const searchable = [
                    finding.ruleId,
                    this.ruleCode(finding.ruleId),
                    finding.title,
                    finding.category,
                    finding.whyItMatters,
                    finding.suggestedFix,
                    ...finding.affectedComponents.flatMap((component) => [
                        component.name,
                        component.filePath,
                        component.evidence,
                        component.codeSnippet
                    ])
                ].join(' ').toLowerCase();
                return matchesLane && matchesSeverity && matchesCategory && matchesType && (!term || searchable.includes(term));
            });
        });
        this.totalFindingOccurrences = computed(() => {
            const current = this.report();
            if (!current) {
                return 0;
            }
            return current.findings.reduce((total, finding) => total + finding.occurrences, 0);
        });
        this.rawJson = computed(() => JSON.stringify(this.report(), null, 2));
    }
    t(key) {
        return TRANSLATIONS[this.selectedLanguage()][key];
    }
    selectLanguage(language) {
        this.selectedLanguage.set(language);
        this.error.set(null);
        this.report.set(null);
        this.resetFilters();
    }
    selectMode(mode) {
        this.uploadMode.set(mode);
        this.error.set(null);
    }
    selectTab(tab) {
        this.activeTab.set(tab);
    }
    onFileSelected(event) {
        const input = event.target;
        this.selectedFile.set(input.files?.[0] ?? null);
        this.error.set(null);
    }
    onFolderSelected(event) {
        const input = event.target;
        const files = Array.from(input.files ?? []);
        this.selectedFolderFiles.set(files);
        this.selectedFolderName.set(this.resolveFolderName(files));
        this.error.set(null);
    }
    scan() {
        this.error.set(null);
        const language = this.selectedLanguage();
        if (this.uploadMode() === 'zip') {
            const file = this.selectedFile();
            if (!file) {
                this.error.set(this.t('selectZipError'));
                return;
            }
            this.currentScanSource.set(file.name);
            this.executeScan(this.api.scanZip(file, language));
            return;
        }
        if (this.uploadMode() === 'folder') {
            const files = this.selectedFolderFiles();
            if (files.length === 0) {
                this.error.set(this.t('selectFolderError'));
                return;
            }
            this.currentScanSource.set(this.selectedFolderName() ?? this.t('selectedFolderFallback'));
            this.executeScan(this.api.scanFolder(files, language));
            return;
        }
        const path = this.localPath.trim();
        if (!path) {
            this.error.set(this.t('selectPathError'));
            return;
        }
        this.currentScanSource.set(path);
        this.executeScan(this.api.scanLocalPath(path, language));
    }
    resetFilters() {
        this.search = '';
        this.severityFilter = 'ALL';
        this.categoryFilter = 'ALL';
        this.typeFilter = 'ALL';
        this.activeDecisionLane.set(null);
        this.touchFilters();
    }
    touchFilters() {
        this.filterVersion.update((version) => version + 1);
    }
    severityPercent(report, severity) {
        const total = report.findings.reduce((sum, finding) => sum + finding.occurrences, 0);
        if (total === 0) {
            return 0;
        }
        return Math.round((this.severityCount(severity) / total) * 100);
    }
    severityTone(severity) {
        return severity.toLowerCase();
    }
    shortPath(value) {
        if (!value) {
            return '-';
        }
        const normalized = value.replace(/\\/g, '/');
        const parts = normalized.split('/').filter(Boolean);
        return parts.slice(-3).join('/');
    }
    decisionCount(report, lane) {
        return report.findings
            .filter((finding) => this.inDecisionLane(finding, lane))
            .reduce((total, finding) => total + finding.occurrences, 0);
    }
    focusDecisionLane(lane) {
        this.resetFilters();
        this.activeDecisionLane.set(lane);
        if (lane === 'ADVISOR') {
            this.activeTab.set('advisor');
            this.touchFilters();
            return;
        }
        this.activeTab.set('findings');
        if (lane === 'BLOCKERS') {
            this.severityFilter = 'CRITICAL';
            this.touchFilters();
            return;
        }
        if (lane === 'IMPORTANT') {
            this.severityFilter = 'MAJOR';
            this.touchFilters();
            return;
        }
        if (lane === 'IMPROVEMENTS') {
            this.severityFilter = 'MINOR';
            this.touchFilters();
            return;
        }
        this.severityFilter = 'INFO';
        this.touchFilters();
    }
    inDecisionLane(finding, lane) {
        const advisor = this.isSpringAdvisorFinding(finding);
        if (lane === 'ADVISOR') {
            return advisor;
        }
        if (advisor) {
            return false;
        }
        if (lane === 'BLOCKERS') {
            return finding.severity === 'CRITICAL';
        }
        if (lane === 'IMPORTANT') {
            return finding.severity === 'MAJOR';
        }
        if (lane === 'IMPROVEMENTS') {
            return finding.severity === 'MINOR';
        }
        return finding.severity === 'INFO';
    }
    setTypeFilter(type) {
        this.search = '';
        this.severityFilter = 'ALL';
        this.categoryFilter = 'ALL';
        this.typeFilter = type;
        this.activeDecisionLane.set(null);
        this.activeTab.set('findings');
        this.touchFilters();
    }
    firstComponent(finding) {
        return finding.affectedComponents.length > 0 ? finding.affectedComponents[0] : null;
    }
    visibleComponents(finding) {
        return finding.affectedComponents.slice(0, 12);
    }
    remainingComponents(finding) {
        return Math.max(0, finding.affectedComponents.length - 12);
    }
    problemTypeRank(type) {
        return {
            SECURITY: 1,
            WEB_LAYER: 2,
            ARCHITECTURE: 3,
            JPA: 4,
            SPRING_BATCH: 5,
            CLOUD_READINESS: 6,
            OBSERVABILITY: 7,
            POM: 8,
            DEPENDENCIES: 9,
            DEPENDENCY_INJECTION: 10,
            RUNTIME_CODE: 11,
            CONFIGURATION: 12,
            TEST: 13,
            CODE: 14
        }[type] ?? 99;
    }
    severityCount(severity) {
        const current = this.report();
        return Number(current?.findingsBySeverity?.[severity] ?? 0);
    }
    scoreClass(score) {
        if (score >= 85)
            return 'score-good';
        if (score >= 65)
            return 'score-warning';
        return 'score-danger';
    }
    exportJson() {
        const current = this.report();
        if (!current) {
            return;
        }
        const blob = new Blob([JSON.stringify(current, null, 2)], { type: 'application/json' });
        const url = URL.createObjectURL(blob);
        const anchor = document.createElement('a');
        anchor.href = url;
        anchor.download = `spring-guardian-${current.projectName || 'report'}.json`;
        anchor.click();
        URL.revokeObjectURL(url);
    }
    trackFinding(_, finding) {
        return finding.ruleId;
    }
    severityLabel(severity) {
        return {
            CRITICAL: this.t('critical'),
            MAJOR: this.t('major'),
            MINOR: this.t('minor'),
            INFO: this.t('info')
        }[severity] ?? this.humanize(severity);
    }
    findingType(finding) {
        if (finding.findingType) {
            return finding.findingType;
        }
        if (this.isSpringAdvisorFinding(finding)) {
            return 'SPRING_ALTERNATIVE';
        }
        if (finding.affectedComponents.some((component) => component.type === 'MAVEN_POM')) {
            return 'POM';
        }
        if (finding.affectedComponents.some((component) => component.type === 'CONFIG_FILE')) {
            return 'CONFIGURATION';
        }
        if (finding.affectedComponents.some((component) => component.type === 'TEST_CLASS')) {
            return 'TEST';
        }
        return 'CODE';
    }
    findingTypeLabel(type) {
        return {
            CODE: this.t('typeCode'),
            POM: this.t('typePom'),
            DEPENDENCIES: this.t('typeDependencies'),
            CONFIGURATION: this.t('typeConfiguration'),
            TEST: this.t('typeTest'),
            SECURITY: this.t('typeSecurity'),
            JPA: this.t('typeJpa'),
            WEB_LAYER: this.t('typeWebLayer'),
            DEPENDENCY_INJECTION: this.t('typeDependencyInjection'),
            RUNTIME_CODE: this.t('typeRuntimeCode'),
            SPRING_ALTERNATIVE: this.t('typeSpringAlternative'),
            SPRING_BATCH: this.t('typeSpringBatch'),
            SPRING_CAPABILITY_GAP: this.t('springModules'),
            ARCHITECTURE: this.t('typeArchitecture'),
            CLOUD_READINESS: this.t('typeCloudReadiness'),
            OBSERVABILITY: this.t('typeObservability')
        }[type] ?? this.humanize(type);
    }
    isSpringAdvisorFinding(finding) {
        return finding.findingType === 'SPRING_ALTERNATIVE' || finding.category === 'Spring Alternative Advisor' || finding.ruleId.startsWith('ADV') || /^SPR(06[4-9]|07[0-9]|08[0-9]|090)/.test(finding.ruleId);
    }
    advisorCount(current) {
        return current.findings.filter((finding) => this.isSpringAdvisorFinding(finding)).reduce((total, finding) => total + finding.occurrences, 0);
    }
    typeOccurrenceCount(current, type) {
        return current.findings
            .filter((finding) => this.findingType(finding) === type)
            .reduce((total, finding) => total + finding.occurrences, 0);
    }
    riskLabel(riskLevel) {
        return {
            HIGH: this.t('riskHigh'),
            MEDIUM: this.t('riskMedium'),
            LOW: this.t('riskLow'),
            HEALTHY: this.t('riskHealthy')
        }[riskLevel] ?? this.humanize(riskLevel);
    }
    statusLabel(status) {
        return {
            INTERVENTO_RICHIESTO: this.t('actionRequired'),
            MIGLIORAMENTO_RICHIESTO: this.t('improvementRequired'),
            NESSUN_PROBLEMA_RILEVATO: this.t('noFindings'),
            REVISIONE_CONSIGLIATA: this.t('reviewRecommended'),
            ACTION_REQUIRED: this.t('actionRequired'),
            IMPROVEMENT_REQUIRED: this.t('improvementRequired'),
            NO_FINDINGS: this.t('noFindings'),
            REVIEW_RECOMMENDED: this.t('reviewRecommended')
        }[status] ?? this.humanize(status);
    }
    componentTypeLabel(type) {
        return {
            PROJECT: this.t('projectComponent'),
            JAVA_CLASS: this.t('javaClass'),
            TEST_CLASS: this.t('testClass'),
            MAVEN_POM: this.t('mavenPom'),
            CONFIG_FILE: this.t('configFile'),
            FILE: this.t('file')
        }[type] ?? this.humanize(type);
    }
    gateStatusLabel(status) {
        return {
            PASS: this.t('pass'),
            FAIL: this.t('fail'),
            WARNING: this.t('warning'),
            OK: this.t('ok'),
            REVIEW: this.t('review'),
            BLOCKED: this.t('blocked'),
            ATTENTION: this.t('attention')
        }[status] ?? this.humanize(status);
    }
    projectTypeLabel(value) {
        return {
            WEB_API: this.t('webApi'),
            BATCH: this.t('batch'),
            LIBRARY: this.t('library'),
            UNKNOWN: this.t('autoDetected')
        }[value] ?? this.humanize(value);
    }
    architectureStyleLabel(value) {
        return {
            AUTO_DETECTED: this.t('autoDetected')
        }[value] ?? this.humanize(value);
    }
    releaseTargetLabel(value) {
        return {
            PRODUCTION: this.t('production'),
            INTERNAL: this.t('internal'),
            LEGACY_BASELINE: this.t('legacyBaseline')
        }[value] ?? this.humanize(value);
    }
    ruleCode(ruleId) {
        return ruleId.match(/^(SPR|SEC|WEB|BAT|CLD|OBS|POM|ADV|ARCH|CAP)\d+/)?.[0] ?? ruleId;
    }
    occurrenceLabel(count) {
        const label = count === 1 ? this.t('occurrence') : this.t('occurrences');
        return `${count} ${label}`;
    }
    folderFilesLabel() {
        const count = this.selectedFolderFiles().length;
        if (count === 0) {
            return '';
        }
        const folder = this.selectedFolderName() ?? this.t('selectedFolderFallback');
        return `${folder} · ${count} ${this.t('file').toLowerCase()}`;
    }
    componentTitle(component) {
        return component.name || component.filePath || this.componentTypeLabel(component.type);
    }
    detectedScopeLabel(current) {
        const modules = [];
        if (current.capabilities.usesSpringWeb)
            modules.push('Web/API');
        if (current.capabilities.usesSpringSecurity)
            modules.push('Security');
        if (current.capabilities.usesSpringBatch)
            modules.push('Batch');
        if (current.capabilities.usesJpa)
            modules.push('JPA');
        if (current.capabilities.usesActuator)
            modules.push('Actuator');
        return modules.length > 0 ? modules.join(' · ') : this.t('autoDetected');
    }
    capabilityItems(current) {
        const capabilities = current.capabilities;
        const values = [
            capabilities.usesSpringWeb ? 'Spring Web' : '',
            capabilities.usesSpringSecurity ? 'Spring Security' : '',
            capabilities.usesJpa ? 'JPA' : '',
            capabilities.usesActuator ? 'Actuator' : '',
            capabilities.usesValidation ? 'Bean Validation' : '',
            capabilities.usesOpenApi ? 'OpenAPI' : '',
            capabilities.usesLombok ? 'Lombok' : '',
            capabilities.usesSpringBatch ? 'Spring Batch' : ''
        ].filter(Boolean);
        return values.length > 0 ? values : [this.t('noFindings')];
    }
    moduleCard(name, active, evidence, description) {
        return {
            name,
            active,
            evidence,
            description,
            status: active ? this.t('capabilityDetected') : this.t('capabilityMissing')
        };
    }
    isProductionFinding(finding) {
        const type = this.findingType(finding);
        return ['CLOUD_READINESS', 'OBSERVABILITY', 'DEPENDENCIES', 'POM', 'CONFIGURATION'].includes(type)
            || finding.ruleId.startsWith('CLD')
            || finding.ruleId.startsWith('OBS')
            || finding.ruleId.startsWith('POM');
    }
    isSuggestionFinding(finding) {
        if (this.isSpringAdvisorFinding(finding) || this.isProductionFinding(finding)) {
            return false;
        }
        return finding.severity === 'INFO' || finding.findingType === 'CODE';
    }
    confidenceLabel(finding) {
        if (finding.ruleId.startsWith('CAP') || this.isSpringAdvisorFinding(finding)) {
            return this.t('highConfidenceLabel');
        }
        if (finding.severity === 'INFO') {
            return this.t('verifyConfidenceLabel');
        }
        return this.t('mediumConfidenceLabel');
    }
    advisorArea(finding) {
        const code = this.ruleCode(finding.ruleId);
        const numeric = Number(code.replace(/\D/g, ''));
        const italian = this.selectedLanguage() === 'it';
        if (['ADV003', 'ADV004', 'ADV005', 'ADV006', 'ADV007', 'ADV048', 'ADV084', 'ADV085', 'ADV089'].includes(code))
            return italian ? 'Client HTTP e integrazioni' : 'HTTP clients and integrations';
        if (['ADV012', 'ADV013', 'ADV049', 'ADV079', 'ADV080', 'ADV097', 'ADV098'].includes(code))
            return italian ? 'Configurazione e proprietà' : 'Configuration and properties';
        if (['ADV008', 'ADV009', 'ADV010', 'ADV011', 'ADV045', 'ADV046', 'ADV057', 'ADV066', 'ADV067', 'ADV086', 'ADV100'].includes(code))
            return italian ? 'Thread, asincronia e schedulazione' : 'Threads, async and scheduling';
        if (['ADV001', 'ADV002', 'ADV041', 'ADV062', 'ADV063', 'ADV083'].includes(code) || code === 'SPR064')
            return italian ? 'JSON e serializzazione' : 'JSON and serialization';
        if (['ADV020', 'ADV021', 'ADV076'].includes(code))
            return italian ? 'Validazione' : 'Validation';
        if (['ADV037', 'ADV038', 'ADV073', 'ADV074', 'ADV075', 'ADV095', 'ADV096'].includes(code))
            return italian ? 'Persistenza e database' : 'Persistence and databases';
        if (['ADV016', 'ADV069', 'ADV070', 'ADV087'].includes(code))
            return italian ? 'Cache e idempotenza' : 'Caching and idempotency';
        if (['ADV028', 'ADV029', 'ADV056', 'ADV072'].includes(code))
            return italian ? 'Eventi e audit' : 'Events and audit';
        if (['ADV026', 'ADV027', 'ADV065'].includes(code))
            return italian ? 'Osservabilità' : 'Observability';
        if (['ADV036', 'ADV058', 'ADV059', 'ADV061', 'ADV090'].includes(code))
            return italian ? 'Sicurezza e filtri' : 'Security and filters';
        if (['ADV042', 'ADV043', 'ADV044', 'ADV060', 'ADV064', 'ADV077', 'ADV078', 'ADV091', 'ADV092'].includes(code))
            return italian ? 'Web/API' : 'Web/API';
        if (['ADV030', 'ADV031', 'ADV051', 'ADV052', 'ADV053', 'ADV093', 'ADV094', 'ADV099'].includes(code))
            return italian ? 'Lifecycle e bean' : 'Lifecycle and beans';
        if (['ADV014', 'ADV015', 'ADV081', 'ADV082', 'ADV071'].includes(code))
            return italian ? 'File, CSV e Batch' : 'Files, CSV and Batch';
        if (numeric >= 1 && numeric <= 100)
            return italian ? 'Modernizzazione Spring' : 'Spring modernization';
        return finding.findingTypeLabel || this.t('typeSpringAlternative');
    }
    executeScan(request$) {
        const scanToken = ++this.scanSequence;
        this.loading.set(true);
        this.error.set(null);
        this.report.set(null);
        this.activeTab.set('overview');
        this.resetFilters();
        request$.subscribe({
            next: (result) => {
                if (scanToken !== this.scanSequence) {
                    return;
                }
                this.report.set(result);
                this.activeTab.set('overview');
                this.loading.set(false);
            },
            error: (error) => {
                if (scanToken !== this.scanSequence) {
                    return;
                }
                const detail = error.error?.detail || error.error?.message || error.message;
                this.error.set(detail || this.t('scanError'));
                this.loading.set(false);
            }
        });
    }
    resolveFolderName(files) {
        const first = files[0];
        const relativePath = first?.webkitRelativePath;
        if (!relativePath) {
            return null;
        }
        return relativePath.split('/')[0] || null;
    }
    humanize(value) {
        const lower = value.replaceAll('_', ' ').toLowerCase();
        return lower.charAt(0).toUpperCase() + lower.slice(1);
    }
    static { this.ɵfac = function AppComponent_Factory(__ngFactoryType__) { return new (__ngFactoryType__ || AppComponent)(i0.ɵɵdirectiveInject(i1.SpringGuardianApiService)); }; }
    static { this.ɵcmp = /*@__PURE__*/ i0.ɵɵdefineComponent({ type: AppComponent, selectors: [["app-root"]], standalone: true, features: [i0.ɵɵStandaloneFeature], decls: 79, vars: 43, consts: [["emptyState", ""], [1, "shell"], [1, "hero"], [1, "hero-copy"], ["aria-label", "Spring Guardian brand", 1, "brand-lockup"], ["src", "assets/spring-guardian-logo.svg", 1, "brand-logo"], [1, "brand-text"], [1, "brand-name"], [1, "brand-subtitle"], [1, "hero-topline"], [1, "eyebrow"], ["role", "group", 1, "language-switch"], ["type", "button", 3, "click"], [1, "hero-text"], [1, "scan-card"], ["role", "tablist", 1, "mode-switch"], ["class", "field", 4, "ngIf"], [1, "auto-analysis-box"], [1, "auto-analysis-head"], [1, "analysis-flow"], [1, "scan-ready-strip"], ["type", "button", 1, "primary", 3, "click", "disabled"], ["class", "error", 4, "ngIf"], [4, "ngIf", "ngIfElse"], [1, "field"], ["for", "zipFile"], ["id", "zipFile", "type", "file", "accept", ".zip", 3, "change"], [4, "ngIf"], ["for", "projectFolder"], ["id", "projectFolder", "type", "file", "webkitdirectory", "", "directory", "", "multiple", "", 3, "change"], ["for", "localPath"], ["id", "localPath", "type", "text", "placeholder", "C:\\progetti\\mio-progetto oppure /scan/mio-progetto", 3, "ngModelChange", "ngModel"], [1, "error"], [1, "score-grid"], [1, "score-card", "main-score"], ["class", "score-card", 4, "ngFor", "ngForOf"], [1, "score-card", "advisor-score"], [1, "score-card"], ["aria-label", "Report impact dashboard", 1, "impact-hub"], [1, "impact-card", "primary-impact"], [1, "impact-card"], [1, "impact-card", "scan-identity"], ["aria-label", "Severity distribution", 1, "severity-map"], [1, "severity-map-copy"], [1, "severity-bars"], ["class", "severity-row", 4, "ngFor", "ngForOf"], ["aria-label", "Priorit\u00E0 di intervento", 1, "decision-board"], [1, "decision-board-title"], [1, "decision-lanes"], ["class", "decision-lane", 4, "ngFor", "ngForOf"], ["aria-label", "Report sections", 1, "tabs"], ["class", "panel-grid", 4, "ngIf"], ["class", "panel", 4, "ngIf"], ["class", "panel advisor-panel", 4, "ngIf"], [1, "severity-row"], [1, "severity-row-head"], [1, "severity-track"], [1, "decision-lane"], ["type", "button", 1, "ghost", 3, "click"], [1, "panel-grid"], [1, "panel", "wide", "readiness"], [1, "panel-title"], [1, "release-grid"], [4, "ngFor", "ngForOf"], [1, "panel", "wide"], [1, "meta-row"], [1, "area-grid"], ["class", "area-card", 4, "ngFor", "ngForOf"], [1, "panel"], [1, "chip-list"], [1, "area-card"], [1, "category-head"], [1, "area-metrics"], [1, "module-grid"], ["class", "module-card", 4, "ngFor", "ngForOf"], ["class", "area-card", 3, "hidden", 4, "ngFor", "ngForOf"], [1, "module-card"], [1, "area-card", 3, "hidden"], [1, "gate-grid"], ["class", "gate-card", 4, "ngFor", "ngForOf"], [1, "gate-card"], [1, "toolbar"], ["type", "search", 3, "ngModelChange", "ngModel", "placeholder"], ["aria-label", "Severity filter", 3, "ngModelChange", "ngModel"], ["value", "ALL"], [3, "value", 4, "ngFor", "ngForOf"], ["aria-label", "Category filter", 3, "ngModelChange", "ngModel"], [3, "ngModelChange", "ngModel"], ["class", "problem-area-strip", 4, "ngIf"], ["class", "active-filter", 4, "ngIf"], [1, "finding-list", "grouped"], ["class", "problem-group", 4, "ngFor", "ngForOf"], [3, "value"], [1, "problem-area-strip"], [1, "strip-title"], ["type", "button", "class", "type-pill", 3, "active", "click", 4, "ngFor", "ngForOf"], ["type", "button", 1, "type-pill", 3, "click"], [1, "active-filter"], [1, "problem-group"], [1, "problem-group-head"], ["class", "finding", 4, "ngFor", "ngForOf", "ngForTrackBy"], [1, "finding"], [1, "finding-header"], [1, "badge"], [1, "category"], [1, "rule-code"], [1, "finding-guidance", "action-oriented"], [1, "guidance-item", "detected"], [1, "guidance-item", "risk"], [1, "guidance-item", "solution"], ["class", "doc-link", "target", "_blank", "rel", "noopener", 3, "href", 4, "ngIf"], ["class", "before-after compact", 4, "ngIf"], ["class", "json-note", 4, "ngIf"], [1, "component-list"], ["class", "component", 4, "ngFor", "ngForOf"], ["target", "_blank", "rel", "noopener", 1, "doc-link", 3, "href"], [1, "before-after", "compact"], [1, "code-evidence"], [1, "json-note"], [1, "component"], ["class", "code-evidence", 4, "ngIf"], [1, "panel", "advisor-panel"], ["class", "empty inline-empty", 4, "ngIf"], ["class", "advisor-group-list", 4, "ngIf"], [1, "empty", "inline-empty"], [1, "advisor-group-list"], ["class", "advisor-group", 4, "ngFor", "ngForOf"], [1, "advisor-group"], [1, "advisor-group-title"], ["class", "finding advisor-finding", 4, "ngFor", "ngForOf", "ngForTrackBy"], [1, "finding", "advisor-finding"], [1, "advisor-flow"], ["class", "real-evidence", 4, "ngIf"], ["class", "before-after", 4, "ngIf"], [1, "real-evidence"], [1, "before-after"], [1, "guidance-grid"], [1, "action-list"], ["class", "action", 4, "ngFor", "ngForOf"], [1, "action"], [1, "priority"], [1, "finding-header", "compact"], [1, "empty"], ["src", "assets/spring-guardian-logo.svg", 1, "empty-logo"]], template: function AppComponent_Template(rf, ctx) { if (rf & 1) {
            const _r1 = i0.ɵɵgetCurrentView();
            i0.ɵɵelementStart(0, "div", 1)(1, "header", 2)(2, "div", 3)(3, "div", 4);
            i0.ɵɵelement(4, "img", 5);
            i0.ɵɵelementStart(5, "div", 6)(6, "span", 7);
            i0.ɵɵtext(7, "Spring Guardian");
            i0.ɵɵelementEnd();
            i0.ɵɵelementStart(8, "span", 8);
            i0.ɵɵtext(9);
            i0.ɵɵelementEnd()()();
            i0.ɵɵelementStart(10, "div", 9)(11, "p", 10);
            i0.ɵɵtext(12);
            i0.ɵɵelementEnd();
            i0.ɵɵelementStart(13, "div", 11)(14, "span");
            i0.ɵɵtext(15);
            i0.ɵɵelementEnd();
            i0.ɵɵelementStart(16, "button", 12);
            i0.ɵɵlistener("click", function AppComponent_Template_button_click_16_listener() { i0.ɵɵrestoreView(_r1); return i0.ɵɵresetView(ctx.selectLanguage("it")); });
            i0.ɵɵtext(17);
            i0.ɵɵelementEnd();
            i0.ɵɵelementStart(18, "button", 12);
            i0.ɵɵlistener("click", function AppComponent_Template_button_click_18_listener() { i0.ɵɵrestoreView(_r1); return i0.ɵɵresetView(ctx.selectLanguage("en")); });
            i0.ɵɵtext(19);
            i0.ɵɵelementEnd()()();
            i0.ɵɵelementStart(20, "h1");
            i0.ɵɵtext(21);
            i0.ɵɵelementEnd();
            i0.ɵɵelementStart(22, "p", 13);
            i0.ɵɵtext(23);
            i0.ɵɵelementEnd()();
            i0.ɵɵelementStart(24, "section", 14)(25, "div", 15)(26, "button", 12);
            i0.ɵɵlistener("click", function AppComponent_Template_button_click_26_listener() { i0.ɵɵrestoreView(_r1); return i0.ɵɵresetView(ctx.selectMode("zip")); });
            i0.ɵɵtext(27);
            i0.ɵɵelementEnd();
            i0.ɵɵelementStart(28, "button", 12);
            i0.ɵɵlistener("click", function AppComponent_Template_button_click_28_listener() { i0.ɵɵrestoreView(_r1); return i0.ɵɵresetView(ctx.selectMode("folder")); });
            i0.ɵɵtext(29);
            i0.ɵɵelementEnd();
            i0.ɵɵelementStart(30, "button", 12);
            i0.ɵɵlistener("click", function AppComponent_Template_button_click_30_listener() { i0.ɵɵrestoreView(_r1); return i0.ɵɵresetView(ctx.selectMode("local")); });
            i0.ɵɵtext(31);
            i0.ɵɵelementEnd()();
            i0.ɵɵtemplate(32, AppComponent_div_32_Template, 6, 3, "div", 16)(33, AppComponent_div_33_Template, 6, 3, "div", 16)(34, AppComponent_div_34_Template, 6, 3, "div", 16);
            i0.ɵɵelementStart(35, "div", 17)(36, "div", 18)(37, "span");
            i0.ɵɵtext(38);
            i0.ɵɵelementEnd();
            i0.ɵɵelementStart(39, "strong");
            i0.ɵɵtext(40);
            i0.ɵɵelementEnd();
            i0.ɵɵelementStart(41, "p");
            i0.ɵɵtext(42);
            i0.ɵɵelementEnd()();
            i0.ɵɵelementStart(43, "div", 19)(44, "div")(45, "b");
            i0.ɵɵtext(46, "01");
            i0.ɵɵelementEnd();
            i0.ɵɵelementStart(47, "span");
            i0.ɵɵtext(48);
            i0.ɵɵelementEnd()();
            i0.ɵɵelementStart(49, "div")(50, "b");
            i0.ɵɵtext(51, "02");
            i0.ɵɵelementEnd();
            i0.ɵɵelementStart(52, "span");
            i0.ɵɵtext(53);
            i0.ɵɵelementEnd()();
            i0.ɵɵelementStart(54, "div")(55, "b");
            i0.ɵɵtext(56, "03");
            i0.ɵɵelementEnd();
            i0.ɵɵelementStart(57, "span");
            i0.ɵɵtext(58);
            i0.ɵɵelementEnd()();
            i0.ɵɵelementStart(59, "div")(60, "b");
            i0.ɵɵtext(61, "04");
            i0.ɵɵelementEnd();
            i0.ɵɵelementStart(62, "span");
            i0.ɵɵtext(63);
            i0.ɵɵelementEnd()()();
            i0.ɵɵelementStart(64, "small");
            i0.ɵɵtext(65);
            i0.ɵɵelementEnd()();
            i0.ɵɵelementStart(66, "div", 20)(67, "span");
            i0.ɵɵtext(68);
            i0.ɵɵelementEnd();
            i0.ɵɵelementStart(69, "strong");
            i0.ɵɵtext(70);
            i0.ɵɵelementEnd();
            i0.ɵɵelementStart(71, "small");
            i0.ɵɵtext(72);
            i0.ɵɵelementEnd()();
            i0.ɵɵelementStart(73, "button", 21);
            i0.ɵɵlistener("click", function AppComponent_Template_button_click_73_listener() { i0.ɵɵrestoreView(_r1); return i0.ɵɵresetView(ctx.scan()); });
            i0.ɵɵtext(74);
            i0.ɵɵelementEnd();
            i0.ɵɵtemplate(75, AppComponent_p_75_Template, 2, 1, "p", 22);
            i0.ɵɵelementEnd()();
            i0.ɵɵtemplate(76, AppComponent_main_76_Template, 112, 79, "main", 23)(77, AppComponent_ng_template_77_Template, 6, 3, "ng-template", null, 0, i0.ɵɵtemplateRefExtractor);
            i0.ɵɵelementEnd();
        } if (rf & 2) {
            const emptyState_r39 = i0.ɵɵreference(78);
            i0.ɵɵadvance(4);
            i0.ɵɵattribute("alt", ctx.t("logoAlt"));
            i0.ɵɵadvance(5);
            i0.ɵɵtextInterpolate(ctx.t("brandSubtitle"));
            i0.ɵɵadvance(3);
            i0.ɵɵtextInterpolate(ctx.t("eyebrow"));
            i0.ɵɵadvance();
            i0.ɵɵattribute("aria-label", ctx.t("language"));
            i0.ɵɵadvance(2);
            i0.ɵɵtextInterpolate(ctx.t("language"));
            i0.ɵɵadvance();
            i0.ɵɵclassProp("active", ctx.selectedLanguage() === "it");
            i0.ɵɵadvance();
            i0.ɵɵtextInterpolate(ctx.t("italian"));
            i0.ɵɵadvance();
            i0.ɵɵclassProp("active", ctx.selectedLanguage() === "en");
            i0.ɵɵadvance();
            i0.ɵɵtextInterpolate(ctx.t("english"));
            i0.ɵɵadvance(2);
            i0.ɵɵtextInterpolate(ctx.t("heroTitle"));
            i0.ɵɵadvance(2);
            i0.ɵɵtextInterpolate(ctx.t("heroText"));
            i0.ɵɵadvance(2);
            i0.ɵɵattribute("aria-label", ctx.t("startScan"));
            i0.ɵɵadvance();
            i0.ɵɵclassProp("active", ctx.uploadMode() === "zip");
            i0.ɵɵadvance();
            i0.ɵɵtextInterpolate(ctx.t("uploadZip"));
            i0.ɵɵadvance();
            i0.ɵɵclassProp("active", ctx.uploadMode() === "folder");
            i0.ɵɵadvance();
            i0.ɵɵtextInterpolate(ctx.t("uploadFolder"));
            i0.ɵɵadvance();
            i0.ɵɵclassProp("active", ctx.uploadMode() === "local");
            i0.ɵɵadvance();
            i0.ɵɵtextInterpolate(ctx.t("backendPath"));
            i0.ɵɵadvance();
            i0.ɵɵproperty("ngIf", ctx.uploadMode() === "zip");
            i0.ɵɵadvance();
            i0.ɵɵproperty("ngIf", ctx.uploadMode() === "folder");
            i0.ɵɵadvance();
            i0.ɵɵproperty("ngIf", ctx.uploadMode() === "local");
            i0.ɵɵadvance(4);
            i0.ɵɵtextInterpolate(ctx.t("scanSettings"));
            i0.ɵɵadvance(2);
            i0.ɵɵtextInterpolate(ctx.t("automaticProfileTitle"));
            i0.ɵɵadvance(2);
            i0.ɵɵtextInterpolate(ctx.t("automaticProfileText"));
            i0.ɵɵadvance();
            i0.ɵɵattribute("aria-label", ctx.t("analysisStepsTitle"));
            i0.ɵɵadvance(5);
            i0.ɵɵtextInterpolate(ctx.t("analysisStepModules"));
            i0.ɵɵadvance(5);
            i0.ɵɵtextInterpolate(ctx.t("analysisStepRules"));
            i0.ɵɵadvance(5);
            i0.ɵɵtextInterpolate(ctx.t("analysisStepAlternatives"));
            i0.ɵɵadvance(5);
            i0.ɵɵtextInterpolate(ctx.t("analysisStepProduction"));
            i0.ɵɵadvance(2);
            i0.ɵɵtextInterpolate(ctx.t("noManualProfile"));
            i0.ɵɵadvance(3);
            i0.ɵɵtextInterpolate(ctx.t("projectType"));
            i0.ɵɵadvance(2);
            i0.ɵɵtextInterpolate(ctx.t("autoDetected"));
            i0.ɵɵadvance(2);
            i0.ɵɵtextInterpolate(ctx.t("profileHelp"));
            i0.ɵɵadvance();
            i0.ɵɵproperty("disabled", ctx.loading());
            i0.ɵɵadvance();
            i0.ɵɵtextInterpolate1(" ", ctx.loading() ? ctx.t("scanRunning") : ctx.t("startScan"), " ");
            i0.ɵɵadvance();
            i0.ɵɵproperty("ngIf", ctx.error());
            i0.ɵɵadvance();
            i0.ɵɵproperty("ngIf", ctx.report())("ngIfElse", emptyState_r39);
        } }, dependencies: [CommonModule, i2.NgForOf, i2.NgIf, i2.DatePipe, FormsModule, i3.NgSelectOption, i3.ɵNgSelectMultipleOption, i3.DefaultValueAccessor, i3.SelectControlValueAccessor, i3.NgControlStatus, i3.NgModel], styles: [".shell[_ngcontent-%COMP%] {\r\n  width: min(1440px, calc(100% - 40px));\r\n  margin: 0 auto;\r\n  padding: 32px 0 56px;\r\n}\r\n\r\n.hero[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  grid-template-columns: minmax(0, 1fr) 460px;\r\n  gap: 28px;\r\n  align-items: stretch;\r\n  margin-bottom: 28px;\r\n}\r\n\r\n.hero-topline[_ngcontent-%COMP%] {\r\n  display: flex;\r\n  align-items: center;\r\n  justify-content: space-between;\r\n  gap: 16px;\r\n  flex-wrap: wrap;\r\n}\r\n\r\n.eyebrow[_ngcontent-%COMP%] {\r\n  margin: 0 0 10px;\r\n  color: var(--brand);\r\n  font-weight: 800;\r\n  letter-spacing: .14em;\r\n  text-transform: uppercase;\r\n}\r\n\r\nh1[_ngcontent-%COMP%] {\r\n  margin: 0;\r\n  font-size: clamp(44px, 8vw, 92px);\r\n  line-height: .9;\r\n  letter-spacing: -0.08em;\r\n}\r\n\r\nh2[_ngcontent-%COMP%], h3[_ngcontent-%COMP%], h4[_ngcontent-%COMP%], p[_ngcontent-%COMP%] { margin-top: 0; }\r\n\r\n.hero-text[_ngcontent-%COMP%] {\r\n  max-width: 780px;\r\n  color: var(--muted);\r\n  font-size: 19px;\r\n  line-height: 1.7;\r\n  margin: 22px 0 0;\r\n}\r\n\r\n.scan-card[_ngcontent-%COMP%], \r\n.panel[_ngcontent-%COMP%], \r\n.score-card[_ngcontent-%COMP%], \r\n.empty[_ngcontent-%COMP%] {\r\n  border: 1px solid var(--border);\r\n  background: linear-gradient(145deg, rgba(255, 255, 255, .09), rgba(255, 255, 255, .045));\r\n  box-shadow: 0 22px 90px rgba(0, 0, 0, .26);\r\n  backdrop-filter: blur(18px);\r\n  border-radius: 28px;\r\n}\r\n\r\n.scan-card[_ngcontent-%COMP%] {\r\n  padding: 22px;\r\n  display: flex;\r\n  flex-direction: column;\r\n  gap: 18px;\r\n}\r\n\r\n.language-switch[_ngcontent-%COMP%], \r\n.mode-switch[_ngcontent-%COMP%], \r\n.tabs[_ngcontent-%COMP%] {\r\n  display: flex;\r\n  gap: 8px;\r\n  padding: 6px;\r\n  border-radius: 18px;\r\n  background: rgba(255,255,255,.06);\r\n  border: 1px solid var(--border);\r\n}\r\n\r\n.language-switch[_ngcontent-%COMP%]   button[_ngcontent-%COMP%], \r\n.mode-switch[_ngcontent-%COMP%]   button[_ngcontent-%COMP%], \r\n.tabs[_ngcontent-%COMP%]   button[_ngcontent-%COMP%], \r\n.ghost[_ngcontent-%COMP%] {\r\n  color: var(--text);\r\n  background: transparent;\r\n  border: 0;\r\n  border-radius: 14px;\r\n  padding: 10px 12px;\r\n}\r\n\r\n.language-switch[_ngcontent-%COMP%]   button.active[_ngcontent-%COMP%], \r\n.mode-switch[_ngcontent-%COMP%]   button.active[_ngcontent-%COMP%], \r\n.tabs[_ngcontent-%COMP%]   button.active[_ngcontent-%COMP%] {\r\n  background: rgba(52, 211, 153, .18);\r\n  color: #bbf7d0;\r\n  box-shadow: inset 0 0 0 1px rgba(187, 247, 208, .22);\r\n}\r\n\r\n.language-switch[_ngcontent-%COMP%] {\r\n  align-items: center;\r\n}\r\n\r\n.language-switch[_ngcontent-%COMP%]   span[_ngcontent-%COMP%] {\r\n  color: var(--muted);\r\n  font-size: 12px;\r\n  font-weight: 800;\r\n  text-transform: uppercase;\r\n  letter-spacing: .08em;\r\n  padding: 0 6px;\r\n}\r\n\r\n.field[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  gap: 8px;\r\n}\r\n\r\nlabel[_ngcontent-%COMP%] {\r\n  color: var(--muted);\r\n  font-size: 13px;\r\n  font-weight: 700;\r\n  text-transform: uppercase;\r\n  letter-spacing: .08em;\r\n}\r\n\r\ninput[_ngcontent-%COMP%], \r\nselect[_ngcontent-%COMP%] {\r\n  width: 100%;\r\n  border: 1px solid var(--border);\r\n  color: var(--text);\r\n  background: rgba(0, 0, 0, .22);\r\n  border-radius: 16px;\r\n  padding: 13px 14px;\r\n  outline: none;\r\n}\r\n\r\nsmall[_ngcontent-%COMP%] { color: var(--muted); line-height: 1.45; }\r\n\r\n.primary[_ngcontent-%COMP%] {\r\n  border: 0;\r\n  border-radius: 18px;\r\n  background: linear-gradient(135deg, var(--brand), var(--brand-strong));\r\n  color: #062018;\r\n  font-weight: 900;\r\n  padding: 14px 18px;\r\n}\r\n\r\n.primary[_ngcontent-%COMP%]:disabled { opacity: .65; cursor: progress; }\r\n.error[_ngcontent-%COMP%] { color: #fecdd3; margin: 0; }\r\n\r\n.score-grid[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  grid-template-columns: 1.4fr repeat(6, 1fr);\r\n  gap: 14px;\r\n  margin-bottom: 18px;\r\n}\r\n\r\n.score-card[_ngcontent-%COMP%] {\r\n  padding: 18px;\r\n  min-height: 126px;\r\n}\r\n\r\n.score-card[_ngcontent-%COMP%]   span[_ngcontent-%COMP%], \r\n.meta-row[_ngcontent-%COMP%]   span[_ngcontent-%COMP%], \r\n.panel-title[_ngcontent-%COMP%]   span[_ngcontent-%COMP%], \r\n.category[_ngcontent-%COMP%], \r\n.rule-code[_ngcontent-%COMP%], \r\n.component[_ngcontent-%COMP%]   span[_ngcontent-%COMP%], \r\n.finding-header[_ngcontent-%COMP%]   strong[_ngcontent-%COMP%] {\r\n  color: var(--muted);\r\n}\r\n\r\n.score-card[_ngcontent-%COMP%]   strong[_ngcontent-%COMP%] {\r\n  display: block;\r\n  font-size: 36px;\r\n  margin: 8px 0;\r\n}\r\n\r\n.main-score[_ngcontent-%COMP%]   strong[_ngcontent-%COMP%] { font-size: 56px; }\r\n.score-good[_ngcontent-%COMP%] { color: var(--brand); }\r\n.score-warning[_ngcontent-%COMP%] { color: var(--warning); }\r\n.score-danger[_ngcontent-%COMP%] { color: var(--danger); }\r\n\r\n.tabs[_ngcontent-%COMP%] { margin-bottom: 18px; width: fit-content; }\r\n\r\n.panel-grid[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  grid-template-columns: 1.2fr .8fr;\r\n  gap: 18px;\r\n}\r\n\r\n.panel[_ngcontent-%COMP%] {\r\n  padding: 24px;\r\n}\r\n\r\n.wide[_ngcontent-%COMP%] { grid-column: 1 / -1; }\r\n\r\n.meta-row[_ngcontent-%COMP%], \r\n.panel-title[_ngcontent-%COMP%], \r\n.finding-header[_ngcontent-%COMP%], \r\n.toolbar[_ngcontent-%COMP%] {\r\n  display: flex;\r\n  align-items: center;\r\n  justify-content: space-between;\r\n  gap: 12px;\r\n  flex-wrap: wrap;\r\n}\r\n\r\n.category-list[_ngcontent-%COMP%], \r\n.finding-list[_ngcontent-%COMP%], \r\n.action-list[_ngcontent-%COMP%], \r\n.component-list[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  gap: 14px;\r\n}\r\n\r\n.category-head[_ngcontent-%COMP%] {\r\n  display: flex;\r\n  justify-content: space-between;\r\n  gap: 12px;\r\n}\r\n\r\n.category-list[_ngcontent-%COMP%]   p[_ngcontent-%COMP%], \r\n.finding[_ngcontent-%COMP%]   p[_ngcontent-%COMP%], \r\n.action[_ngcontent-%COMP%]   p[_ngcontent-%COMP%], \r\n.component[_ngcontent-%COMP%]   p[_ngcontent-%COMP%], \r\n.panel[_ngcontent-%COMP%]   p[_ngcontent-%COMP%], \r\nli[_ngcontent-%COMP%] {\r\n  color: var(--muted);\r\n  line-height: 1.6;\r\n}\r\n\r\n.toolbar[_ngcontent-%COMP%] {\r\n  margin-bottom: 20px;\r\n  align-items: stretch;\r\n}\r\n\r\n.toolbar[_ngcontent-%COMP%]   input[_ngcontent-%COMP%] { min-width: min(420px, 100%); flex: 1; }\r\n.toolbar[_ngcontent-%COMP%]   select[_ngcontent-%COMP%] { width: auto; min-width: 180px; }\r\n.ghost[_ngcontent-%COMP%] { border: 1px solid var(--border); background: rgba(255,255,255,.05); }\r\n\r\n.finding[_ngcontent-%COMP%], \r\n.action[_ngcontent-%COMP%], \r\n.component[_ngcontent-%COMP%] {\r\n  border: 1px solid var(--border);\r\n  background: rgba(0,0,0,.16);\r\n  border-radius: 22px;\r\n  padding: 18px;\r\n}\r\n\r\n.finding[_ngcontent-%COMP%]   h3[_ngcontent-%COMP%], \r\n.action[_ngcontent-%COMP%]   h3[_ngcontent-%COMP%] { margin: 14px 0 8px; }\r\n\r\n.badge[_ngcontent-%COMP%] {\r\n  display: inline-flex;\r\n  border-radius: 999px;\r\n  padding: 5px 9px;\r\n  font-size: 12px;\r\n  font-weight: 900;\r\n  margin-right: 8px;\r\n}\r\n\r\n.badge[data-severity=\"CRITICAL\"][_ngcontent-%COMP%] { background: rgba(251, 113, 133, .18); color: #fecdd3; }\r\n.badge[data-severity=\"MAJOR\"][_ngcontent-%COMP%] { background: rgba(251, 191, 36, .18); color: #fde68a; }\r\n.badge[data-severity=\"MINOR\"][_ngcontent-%COMP%] { background: rgba(167, 139, 250, .18); color: #ddd6fe; }\r\n.badge[data-severity=\"INFO\"][_ngcontent-%COMP%] { background: rgba(96, 165, 250, .18); color: #bfdbfe; }\r\n\r\n.rule-code[_ngcontent-%COMP%] {\r\n  display: inline-flex;\r\n  align-items: center;\r\n  border-radius: 999px;\r\n  padding: 4px 8px;\r\n  background: rgba(255, 255, 255, .055);\r\n  font-size: 12px;\r\n}\r\n\r\n.json-note[_ngcontent-%COMP%] {\r\n  margin-top: -6px;\r\n}\r\n\r\n.explain-grid[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  grid-template-columns: repeat(2, minmax(0, 1fr));\r\n  gap: 14px;\r\n  margin: 16px 0;\r\n}\r\n\r\n.explain-grid[_ngcontent-%COMP%]    > div[_ngcontent-%COMP%] {\r\n  border-radius: 18px;\r\n  padding: 14px;\r\n  background: rgba(255,255,255,.045);\r\n}\r\n\r\nsummary[_ngcontent-%COMP%] {\r\n  color: #bbf7d0;\r\n  font-weight: 800;\r\n  cursor: pointer;\r\n}\r\n\r\n.component-list[_ngcontent-%COMP%] { margin-top: 14px; }\r\n.component[_ngcontent-%COMP%]   strong[_ngcontent-%COMP%], \r\n.component[_ngcontent-%COMP%]   span[_ngcontent-%COMP%] { display: block; overflow-wrap: anywhere; }\r\n\r\n.action[_ngcontent-%COMP%] { display: grid; grid-template-columns: auto 1fr; gap: 16px; }\r\n.priority[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  place-items: center;\r\n  width: 52px;\r\n  height: 52px;\r\n  border-radius: 18px;\r\n  background: rgba(52, 211, 153, .16);\r\n  color: #bbf7d0;\r\n  font-weight: 900;\r\n}\r\n\r\n.compact[_ngcontent-%COMP%] { justify-content: flex-start; }\r\n\r\npre[_ngcontent-%COMP%] {\r\n  overflow: auto;\r\n  max-height: 72vh;\r\n  border-radius: 20px;\r\n  padding: 18px;\r\n  background: #020617;\r\n  border: 1px solid var(--border);\r\n  color: #d1fae5;\r\n}\r\n\r\n.empty[_ngcontent-%COMP%] {\r\n  padding: 44px;\r\n  text-align: center;\r\n}\r\n\r\n@media (max-width: 1100px) {\r\n  .hero[_ngcontent-%COMP%], \r\n   .panel-grid[_ngcontent-%COMP%], \r\n   .explain-grid[_ngcontent-%COMP%] {\r\n    grid-template-columns: 1fr;\r\n  }\r\n\r\n  .score-grid[_ngcontent-%COMP%] {\r\n    grid-template-columns: repeat(2, 1fr);\r\n  }\r\n}\r\n\r\n@media (max-width: 700px) {\r\n  .shell[_ngcontent-%COMP%] { width: min(100% - 24px, 1440px); padding-top: 18px; }\r\n  .score-grid[_ngcontent-%COMP%] { grid-template-columns: 1fr; }\r\n  .toolbar[_ngcontent-%COMP%] { display: grid; }\r\n  .toolbar[_ngcontent-%COMP%]   select[_ngcontent-%COMP%] { width: 100%; }\r\n}\r\n\r\n.profile-box[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  gap: 12px;\r\n  border: 1px solid var(--border);\r\n  border-radius: 22px;\r\n  padding: 14px;\r\n  background: rgba(0,0,0,.14);\r\n}\r\n\r\n.profile-box[_ngcontent-%COMP%]   h2[_ngcontent-%COMP%] {\r\n  margin: 0;\r\n  font-size: 16px;\r\n}\r\n\r\n.profile-grid[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  grid-template-columns: repeat(2, minmax(0, 1fr));\r\n  gap: 12px;\r\n}\r\n\r\n.radio-group[_ngcontent-%COMP%] {\r\n  display: flex;\r\n  flex-wrap: wrap;\r\n  gap: 8px;\r\n}\r\n\r\n.radio-card[_ngcontent-%COMP%] {\r\n  display: inline-flex;\r\n  align-items: center;\r\n  gap: 8px;\r\n  padding: 10px 12px;\r\n  border: 1px solid var(--border);\r\n  border-radius: 14px;\r\n  color: var(--text);\r\n  background: rgba(0, 0, 0, .18);\r\n  text-transform: none;\r\n  letter-spacing: 0;\r\n  cursor: pointer;\r\n}\r\n\r\n.radio-card[_ngcontent-%COMP%]:has(input:checked) {\r\n  color: #bbf7d0;\r\n  background: rgba(52, 211, 153, .18);\r\n  border-color: rgba(187, 247, 208, .28);\r\n}\r\n\r\n.radio-card[_ngcontent-%COMP%]   input[_ngcontent-%COMP%] {\r\n  width: auto;\r\n}\r\n\r\n.check-row[_ngcontent-%COMP%] {\r\n  display: flex;\r\n  align-items: center;\r\n  gap: 10px;\r\n  text-transform: none;\r\n  letter-spacing: 0;\r\n  color: var(--text);\r\n}\r\n\r\n.check-row[_ngcontent-%COMP%]   input[_ngcontent-%COMP%] {\r\n  width: auto;\r\n}\r\n\r\n.readiness[data-status=\"NOT_READY\"][_ngcontent-%COMP%] {\r\n  box-shadow: inset 0 0 0 1px rgba(251, 113, 133, .32), 0 22px 90px rgba(0, 0, 0, .26);\r\n}\r\n\r\n.readiness[data-status=\"READY_WITH_WARNINGS\"][_ngcontent-%COMP%] {\r\n  box-shadow: inset 0 0 0 1px rgba(251, 191, 36, .28), 0 22px 90px rgba(0, 0, 0, .26);\r\n}\r\n\r\n.readiness[data-status=\"READY\"][_ngcontent-%COMP%] {\r\n  box-shadow: inset 0 0 0 1px rgba(52, 211, 153, .28), 0 22px 90px rgba(0, 0, 0, .26);\r\n}\r\n\r\n.release-grid[_ngcontent-%COMP%], \r\n.gate-grid[_ngcontent-%COMP%], \r\n.area-grid[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  grid-template-columns: repeat(2, minmax(0, 1fr));\r\n  gap: 14px;\r\n}\r\n\r\n.gate-card[_ngcontent-%COMP%], \r\n.area-card[_ngcontent-%COMP%] {\r\n  border: 1px solid var(--border);\r\n  background: rgba(0,0,0,.16);\r\n  border-radius: 22px;\r\n  padding: 18px;\r\n}\r\n\r\n.gate-card[data-status=\"FAIL\"][_ngcontent-%COMP%], \r\n.area-card[data-status=\"BLOCKED\"][_ngcontent-%COMP%] {\r\n  border-color: rgba(251, 113, 133, .44);\r\n}\r\n\r\n.gate-card[data-status=\"WARNING\"][_ngcontent-%COMP%], \r\n.area-card[data-status=\"ATTENTION\"][_ngcontent-%COMP%] {\r\n  border-color: rgba(251, 191, 36, .36);\r\n}\r\n\r\n.gate-card[data-status=\"PASS\"][_ngcontent-%COMP%], \r\n.area-card[data-status=\"OK\"][_ngcontent-%COMP%] {\r\n  border-color: rgba(52, 211, 153, .3);\r\n}\r\n\r\n.area-metrics[_ngcontent-%COMP%], \r\n.chip-list[_ngcontent-%COMP%] {\r\n  display: flex;\r\n  flex-wrap: wrap;\r\n  gap: 8px;\r\n}\r\n\r\n.area-metrics[_ngcontent-%COMP%]   span[_ngcontent-%COMP%], \r\n.chip-list[_ngcontent-%COMP%]   span[_ngcontent-%COMP%] {\r\n  display: inline-flex;\r\n  border-radius: 999px;\r\n  padding: 6px 10px;\r\n  color: var(--text);\r\n  background: rgba(255, 255, 255, .06);\r\n  border: 1px solid var(--border);\r\n  font-size: 13px;\r\n}\r\n\r\n@media (max-width: 1100px) {\r\n  .profile-grid[_ngcontent-%COMP%], \r\n   .release-grid[_ngcontent-%COMP%], \r\n   .gate-grid[_ngcontent-%COMP%], \r\n   .area-grid[_ngcontent-%COMP%] {\r\n    grid-template-columns: 1fr;\r\n  }\r\n}\r\n\r\n.advisor-score[_ngcontent-%COMP%] {\r\n  box-shadow: inset 0 0 0 1px rgba(52, 211, 153, .2), 0 22px 90px rgba(0, 0, 0, .26);\r\n}\r\n\r\n.advisor-panel[_ngcontent-%COMP%]   .panel-title[_ngcontent-%COMP%] {\r\n  align-items: flex-start;\r\n}\r\n\r\n.advisor-panel[_ngcontent-%COMP%]   .panel-title[_ngcontent-%COMP%]   p[_ngcontent-%COMP%] {\r\n  margin-bottom: 0;\r\n}\r\n\r\n.type-code[_ngcontent-%COMP%] {\r\n  color: #bbf7d0;\r\n}\r\n\r\n.inline-empty[_ngcontent-%COMP%] {\r\n  box-shadow: none;\r\n  background: rgba(0, 0, 0, .14);\r\n  padding: 28px;\r\n}\r\n\r\n.type-card[_ngcontent-%COMP%], \r\n.advisor-finding[_ngcontent-%COMP%] {\r\n  position: relative;\r\n  overflow: hidden;\r\n}\r\n\r\n.type-card[_ngcontent-%COMP%]::before, \r\n.advisor-finding[_ngcontent-%COMP%]::before {\r\n  content: '';\r\n  position: absolute;\r\n  inset: 0 auto 0 0;\r\n  width: 4px;\r\n  background: linear-gradient(180deg, var(--brand), transparent);\r\n  opacity: .75;\r\n}\r\n\r\n.score-grid[_ngcontent-%COMP%]   .score-card[_ngcontent-%COMP%] {\r\n  transition: transform .18s ease, border-color .18s ease, background .18s ease;\r\n}\r\n\r\n.score-grid[_ngcontent-%COMP%]   .score-card[_ngcontent-%COMP%]:hover, \r\n.gate-card[_ngcontent-%COMP%]:hover, \r\n.area-card[_ngcontent-%COMP%]:hover, \r\n.finding[_ngcontent-%COMP%]:hover {\r\n  transform: translateY(-2px);\r\n  border-color: rgba(187, 247, 208, .28);\r\n  background: linear-gradient(145deg, rgba(255, 255, 255, .11), rgba(255, 255, 255, .05));\r\n}\r\n\r\n.rule-code[_ngcontent-%COMP%] {\r\n  border: 1px solid var(--border);\r\n  border-radius: 999px;\r\n  padding: 4px 8px;\r\n  background: rgba(255,255,255,.045);\r\n}\r\n\r\n\r\n.code-evidence[_ngcontent-%COMP%] {\r\n  margin: 10px 0 0;\r\n  padding: 12px 14px;\r\n  overflow-x: auto;\r\n  border: 1px solid rgba(15, 23, 42, 0.12);\r\n  border-radius: 12px;\r\n  background: #0f172a;\r\n  color: #e5e7eb;\r\n  font-size: 0.82rem;\r\n  line-height: 1.55;\r\n  white-space: pre;\r\n}\r\n\r\n.code-evidence[_ngcontent-%COMP%]   code[_ngcontent-%COMP%] {\r\n  font-family: \"JetBrains Mono\", \"Fira Code\", Consolas, monospace;\r\n}\r\n\r\n\n\r\n.score-grid[_ngcontent-%COMP%] {\r\n  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));\r\n}\r\n\r\n.main-score[_ngcontent-%COMP%] {\r\n  grid-column: span 2;\r\n}\r\n\r\n.decision-board[_ngcontent-%COMP%] {\r\n  border: 1px solid var(--border);\r\n  background:\r\n    radial-gradient(circle at top left, rgba(52, 211, 153, .16), transparent 36%),\r\n    linear-gradient(145deg, rgba(255,255,255,.10), rgba(255,255,255,.045));\r\n  box-shadow: 0 22px 90px rgba(0, 0, 0, .24);\r\n  backdrop-filter: blur(18px);\r\n  border-radius: 28px;\r\n  padding: 22px;\r\n  margin: 0 0 18px;\r\n}\r\n\r\n.decision-board-title[_ngcontent-%COMP%] {\r\n  display: flex;\r\n  justify-content: space-between;\r\n  gap: 16px;\r\n  margin-bottom: 16px;\r\n}\r\n\r\n.decision-board-title[_ngcontent-%COMP%]   h2[_ngcontent-%COMP%] {\r\n  margin-bottom: 6px;\r\n}\r\n\r\n.decision-board-title[_ngcontent-%COMP%]   p[_ngcontent-%COMP%] {\r\n  color: var(--muted);\r\n  margin-bottom: 0;\r\n  line-height: 1.55;\r\n}\r\n\r\n.decision-lanes[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  grid-template-columns: repeat(5, minmax(0, 1fr));\r\n  gap: 12px;\r\n}\r\n\r\n.decision-lane[_ngcontent-%COMP%] {\r\n  position: relative;\r\n  overflow: hidden;\r\n  border: 1px solid var(--border);\r\n  background: rgba(0,0,0,.18);\r\n  border-radius: 22px;\r\n  padding: 16px;\r\n  min-height: 178px;\r\n  display: flex;\r\n  flex-direction: column;\r\n  gap: 10px;\r\n}\r\n\r\n.decision-lane[_ngcontent-%COMP%]::before {\r\n  content: '';\r\n  position: absolute;\r\n  inset: 0 auto 0 0;\r\n  width: 5px;\r\n  background: rgba(148, 163, 184, .8);\r\n}\r\n\r\n.decision-lane[data-lane=\"BLOCKERS\"][_ngcontent-%COMP%]::before { background: #fb7185; }\r\n.decision-lane[data-lane=\"IMPORTANT\"][_ngcontent-%COMP%]::before { background: #fbbf24; }\r\n.decision-lane[data-lane=\"IMPROVEMENTS\"][_ngcontent-%COMP%]::before { background: #60a5fa; }\r\n.decision-lane[data-lane=\"ADVISOR\"][_ngcontent-%COMP%]::before { background: #34d399; }\r\n.decision-lane[data-lane=\"INFORMATION\"][_ngcontent-%COMP%]::before { background: #a78bfa; }\r\n\r\n.decision-lane[_ngcontent-%COMP%]   span[_ngcontent-%COMP%] {\r\n  color: var(--muted);\r\n  font-size: 12px;\r\n  font-weight: 900;\r\n  letter-spacing: .08em;\r\n  text-transform: uppercase;\r\n  padding-left: 4px;\r\n}\r\n\r\n.decision-lane[_ngcontent-%COMP%]   strong[_ngcontent-%COMP%] {\r\n  font-size: 42px;\r\n  line-height: 1;\r\n  letter-spacing: -.05em;\r\n}\r\n\r\n.decision-lane[_ngcontent-%COMP%]   p[_ngcontent-%COMP%] {\r\n  color: var(--muted);\r\n  line-height: 1.45;\r\n  margin-bottom: auto;\r\n}\r\n\r\n.finding[data-decision=\"ADVISOR\"][_ngcontent-%COMP%], \r\n.advisor-finding[_ngcontent-%COMP%] {\r\n  border-color: rgba(52, 211, 153, .26);\r\n}\r\n\r\n.code-evidence[_ngcontent-%COMP%] {\r\n  margin: 12px 0 0;\r\n  padding: 14px;\r\n  border-radius: 16px;\r\n  border: 1px solid rgba(148, 163, 184, .24);\r\n  background: rgba(2, 6, 23, .72);\r\n  overflow: auto;\r\n  color: #dbeafe;\r\n  line-height: 1.55;\r\n  font-size: 13px;\r\n}\r\n\r\n.code-evidence[_ngcontent-%COMP%]   code[_ngcontent-%COMP%] {\r\n  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', monospace;\r\n  white-space: pre;\r\n}\r\n\r\n@media (max-width: 1100px) {\r\n  .decision-lanes[_ngcontent-%COMP%] {\r\n    grid-template-columns: repeat(2, minmax(0, 1fr));\r\n  }\r\n\r\n  .main-score[_ngcontent-%COMP%] {\r\n    grid-column: span 1;\r\n  }\r\n}\r\n\r\n@media (max-width: 720px) {\r\n  .decision-lanes[_ngcontent-%COMP%] {\r\n    grid-template-columns: 1fr;\r\n  }\r\n}\r\n\r\n\n\r\n.finding-guidance[_ngcontent-%COMP%], \r\n.advisor-flow[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  grid-template-columns: repeat(3, minmax(0, 1fr));\r\n  gap: 12px;\r\n  margin: 16px 0;\r\n}\r\n\r\n.advisor-flow[_ngcontent-%COMP%] {\r\n  grid-template-columns: repeat(2, minmax(0, 1fr));\r\n}\r\n\r\n.guidance-item[_ngcontent-%COMP%], \r\n.advisor-flow[_ngcontent-%COMP%]    > div[_ngcontent-%COMP%] {\r\n  border: 1px solid rgba(148, 163, 184, .18);\r\n  border-radius: 18px;\r\n  padding: 14px;\r\n  background: rgba(255,255,255,.045);\r\n}\r\n\r\n.guidance-item[_ngcontent-%COMP%]   span[_ngcontent-%COMP%], \r\n.advisor-flow[_ngcontent-%COMP%]   span[_ngcontent-%COMP%], \r\n.advisor-group-title[_ngcontent-%COMP%]   span[_ngcontent-%COMP%] {\r\n  display: inline-flex;\r\n  color: #bbf7d0;\r\n  font-size: 12px;\r\n  font-weight: 900;\r\n  letter-spacing: .08em;\r\n  text-transform: uppercase;\r\n  margin-bottom: 8px;\r\n}\r\n\r\n.guidance-item.detected[_ngcontent-%COMP%] { border-color: rgba(96, 165, 250, .32); }\r\n.guidance-item.risk[_ngcontent-%COMP%] { border-color: rgba(251, 191, 36, .28); }\r\n.guidance-item.solution[_ngcontent-%COMP%] { border-color: rgba(52, 211, 153, .28); }\r\n\r\n.doc-link[_ngcontent-%COMP%] {\r\n  display: inline-flex;\r\n  align-items: center;\r\n  gap: 8px;\r\n  width: fit-content;\r\n  margin: 0 0 14px;\r\n  padding: 8px 12px;\r\n  border-radius: 999px;\r\n  color: #bbf7d0;\r\n  background: rgba(52, 211, 153, .12);\r\n  border: 1px solid rgba(52, 211, 153, .28);\r\n  text-decoration: none;\r\n  font-weight: 800;\r\n}\r\n\r\n.before-after[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  grid-template-columns: repeat(2, minmax(0, 1fr));\r\n  gap: 12px;\r\n  margin: 10px 0 16px;\r\n}\r\n\r\n.before-after[_ngcontent-%COMP%]   h4[_ngcontent-%COMP%] {\r\n  color: var(--muted);\r\n  margin: 0 0 8px;\r\n  font-size: 12px;\r\n  text-transform: uppercase;\r\n  letter-spacing: .08em;\r\n}\r\n\r\n.advisor-group-list[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  gap: 18px;\r\n}\r\n\r\n.advisor-group[_ngcontent-%COMP%] {\r\n  border: 1px solid rgba(52, 211, 153, .20);\r\n  border-radius: 24px;\r\n  padding: 16px;\r\n  background: rgba(0,0,0,.12);\r\n}\r\n\r\n.advisor-group-title[_ngcontent-%COMP%] {\r\n  display: flex;\r\n  align-items: baseline;\r\n  justify-content: space-between;\r\n  gap: 12px;\r\n  margin-bottom: 12px;\r\n}\r\n\r\n.advisor-group-title[_ngcontent-%COMP%]   h3[_ngcontent-%COMP%] {\r\n  margin: 0;\r\n}\r\n\r\n@media (max-width: 1100px) {\r\n  .finding-guidance[_ngcontent-%COMP%], \r\n   .advisor-flow[_ngcontent-%COMP%], \r\n   .before-after[_ngcontent-%COMP%] {\r\n    grid-template-columns: 1fr;\r\n  }\r\n}\r\n\r\n\n\r\n.problem-area-strip[_ngcontent-%COMP%] {\r\n  display: flex;\r\n  align-items: center;\r\n  gap: 10px;\r\n  flex-wrap: wrap;\r\n  margin: 0 0 18px;\r\n  padding: 12px;\r\n  border: 1px solid rgba(148, 163, 184, .18);\r\n  border-radius: 20px;\r\n  background: rgba(255, 255, 255, .035);\r\n}\r\n\r\n.strip-title[_ngcontent-%COMP%] {\r\n  color: var(--muted);\r\n  font-size: 12px;\r\n  font-weight: 900;\r\n  text-transform: uppercase;\r\n  letter-spacing: .08em;\r\n  margin-right: 4px;\r\n}\r\n\r\n.type-pill[_ngcontent-%COMP%] {\r\n  display: inline-flex;\r\n  align-items: center;\r\n  gap: 10px;\r\n  border: 1px solid rgba(148, 163, 184, .20);\r\n  border-radius: 999px;\r\n  padding: 8px 12px;\r\n  color: var(--text);\r\n  background: rgba(15, 23, 42, .46);\r\n  cursor: pointer;\r\n}\r\n\r\n.type-pill.active[_ngcontent-%COMP%], \r\n.type-pill[_ngcontent-%COMP%]:hover {\r\n  border-color: rgba(52, 211, 153, .42);\r\n  background: rgba(52, 211, 153, .14);\r\n}\r\n\r\n.type-pill[_ngcontent-%COMP%]   strong[_ngcontent-%COMP%] {\r\n  display: inline-flex;\r\n  min-width: 26px;\r\n  justify-content: center;\r\n  border-radius: 999px;\r\n  padding: 2px 7px;\r\n  background: rgba(255, 255, 255, .08);\r\n}\r\n\r\n.active-filter[_ngcontent-%COMP%] {\r\n  margin: 0 0 14px;\r\n  padding: 10px 14px;\r\n  border-left: 4px solid #34d399;\r\n  border-radius: 14px;\r\n  background: rgba(52, 211, 153, .10);\r\n  color: var(--muted);\r\n}\r\n\r\n.finding-list.grouped[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  gap: 22px;\r\n}\r\n\r\n.problem-group[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  gap: 14px;\r\n}\r\n\r\n.problem-group-head[_ngcontent-%COMP%] {\r\n  display: flex;\r\n  align-items: center;\r\n  justify-content: space-between;\r\n  padding: 12px 16px;\r\n  border-radius: 18px;\r\n  background: linear-gradient(90deg, rgba(52, 211, 153, .14), rgba(15, 23, 42, .18));\r\n  border: 1px solid rgba(52, 211, 153, .18);\r\n}\r\n\r\n.problem-group-head[_ngcontent-%COMP%]   span[_ngcontent-%COMP%] {\r\n  color: #bbf7d0;\r\n  font-size: 12px;\r\n  font-weight: 900;\r\n  letter-spacing: .08em;\r\n  text-transform: uppercase;\r\n}\r\n\r\n.problem-group-head[_ngcontent-%COMP%]   h3[_ngcontent-%COMP%] {\r\n  margin: 4px 0 0;\r\n}\r\n\r\n.finding[_ngcontent-%COMP%]   .finding-guidance.action-oriented[_ngcontent-%COMP%] {\r\n  grid-template-columns: 1fr;\r\n}\r\n\r\n.finding[_ngcontent-%COMP%]   .guidance-item[_ngcontent-%COMP%] {\r\n  padding: 12px 14px;\r\n}\r\n\r\n.finding[_ngcontent-%COMP%]   .guidance-item[_ngcontent-%COMP%]   p[_ngcontent-%COMP%] {\r\n  margin: 0;\r\n  line-height: 1.55;\r\n}\r\n\r\n.before-after.compact[_ngcontent-%COMP%] {\r\n  margin-top: 14px;\r\n}\r\n\r\n.before-after.compact[_ngcontent-%COMP%]   .code-evidence[_ngcontent-%COMP%] {\r\n  max-height: 260px;\r\n  overflow: auto;\r\n}\r\n\r\n\r\n\r\n\n\r\n.hero-copy[_ngcontent-%COMP%] {\r\n  display: flex;\r\n  flex-direction: column;\r\n  justify-content: space-between;\r\n  min-width: 0;\r\n}\r\n\r\n.brand-lockup[_ngcontent-%COMP%] {\r\n  display: inline-flex;\r\n  align-items: center;\r\n  gap: 14px;\r\n  width: fit-content;\r\n  padding: 10px 14px 10px 10px;\r\n  margin-bottom: 18px;\r\n  border: 1px solid rgba(187, 247, 208, .18);\r\n  border-radius: 24px;\r\n  background: linear-gradient(135deg, rgba(15, 23, 42, .72), rgba(6, 78, 59, .34));\r\n  box-shadow: 0 18px 70px rgba(0, 0, 0, .22);\r\n}\r\n\r\n.brand-logo[_ngcontent-%COMP%] {\r\n  width: 68px;\r\n  height: 68px;\r\n  flex: 0 0 auto;\r\n  border-radius: 18px;\r\n  box-shadow: 0 14px 36px rgba(16, 185, 129, .18);\r\n}\r\n\r\n.brand-text[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  gap: 3px;\r\n}\r\n\r\n.brand-name[_ngcontent-%COMP%] {\r\n  color: #ecfdf5;\r\n  font-weight: 950;\r\n  font-size: 20px;\r\n  letter-spacing: -.04em;\r\n}\r\n\r\n.brand-subtitle[_ngcontent-%COMP%] {\r\n  color: #86efac;\r\n  font-size: 11px;\r\n  font-weight: 900;\r\n  letter-spacing: .16em;\r\n  text-transform: uppercase;\r\n}\r\n\r\n.hero-brand-card[_ngcontent-%COMP%] {\r\n  width: min(100%, 720px);\r\n  margin-top: 24px;\r\n  border-radius: 34px;\r\n  overflow: hidden;\r\n  border: 1px solid rgba(187, 247, 208, .14);\r\n  box-shadow: 0 22px 90px rgba(0, 0, 0, .28);\r\n}\r\n\r\n.hero-brand-card[_ngcontent-%COMP%]   img[_ngcontent-%COMP%] {\r\n  display: block;\r\n  width: 100%;\r\n  height: auto;\r\n}\r\n\r\n.empty-logo[_ngcontent-%COMP%] {\r\n  width: 116px;\r\n  height: 116px;\r\n  margin-bottom: 18px;\r\n  border-radius: 28px;\r\n  box-shadow: 0 18px 60px rgba(16, 185, 129, .2);\r\n}\r\n\r\n@media (max-width: 700px) {\r\n  .brand-lockup[_ngcontent-%COMP%] {\r\n    width: 100%;\r\n  }\r\n\r\n  .brand-logo[_ngcontent-%COMP%] {\r\n    width: 56px;\r\n    height: 56px;\r\n  }\r\n\r\n  .brand-name[_ngcontent-%COMP%] {\r\n    font-size: 18px;\r\n  }\r\n\r\n  .brand-subtitle[_ngcontent-%COMP%] {\r\n    font-size: 10px;\r\n    letter-spacing: .1em;\r\n  }\r\n}\r\n\r\n\r\n.real-evidence[_ngcontent-%COMP%] {\r\n  margin: 18px 0;\r\n  padding: 14px 16px;\r\n  border: 1px solid rgba(148, 163, 184, 0.28);\r\n  border-radius: 16px;\r\n  background: rgba(15, 23, 42, 0.35);\r\n}\r\n\r\n.real-evidence[_ngcontent-%COMP%]   h4[_ngcontent-%COMP%] {\r\n  margin: 0 0 8px;\r\n  color: #bbf7d0;\r\n  font-size: 0.78rem;\r\n  letter-spacing: 0.06em;\r\n  text-transform: uppercase;\r\n}\r\n\r\n.real-evidence[_ngcontent-%COMP%]   p[_ngcontent-%COMP%] {\r\n  margin: 6px 0;\r\n}\r\n\r\n\n\r\n.hero[_ngcontent-%COMP%] {\r\n  position: relative;\r\n}\r\n\r\n.hero[_ngcontent-%COMP%]::before {\r\n  content: '';\r\n  position: absolute;\r\n  inset: -24px -24px auto -24px;\r\n  height: 420px;\r\n  pointer-events: none;\r\n  background:\r\n    radial-gradient(circle at 16% 18%, rgba(52, 211, 153, .22), transparent 34%),\r\n    radial-gradient(circle at 74% 10%, rgba(96, 165, 250, .18), transparent 30%);\r\n  filter: blur(4px);\r\n  z-index: -1;\r\n}\r\n\r\n.hero-copy[_ngcontent-%COMP%], \r\n.scan-card[_ngcontent-%COMP%], \r\n.score-card[_ngcontent-%COMP%], \r\n.panel[_ngcontent-%COMP%], \r\n.decision-board[_ngcontent-%COMP%], \r\n.impact-card[_ngcontent-%COMP%], \r\n.severity-map[_ngcontent-%COMP%] {\r\n  position: relative;\r\n  overflow: hidden;\r\n}\r\n\r\n.hero-copy[_ngcontent-%COMP%]::after, \r\n.scan-card[_ngcontent-%COMP%]::after, \r\n.panel[_ngcontent-%COMP%]::after, \r\n.score-card[_ngcontent-%COMP%]::after, \r\n.impact-card[_ngcontent-%COMP%]::after, \r\n.severity-map[_ngcontent-%COMP%]::after, \r\n.decision-board[_ngcontent-%COMP%]::after {\r\n  content: '';\r\n  position: absolute;\r\n  inset: 0;\r\n  pointer-events: none;\r\n  background: linear-gradient(120deg, rgba(255,255,255,.16), transparent 28%, transparent 72%, rgba(255,255,255,.055));\r\n  opacity: .38;\r\n}\r\n\r\n.brand-lockup[_ngcontent-%COMP%] {\r\n  display: inline-flex;\r\n  align-items: center;\r\n  gap: 14px;\r\n  padding: 10px 14px;\r\n  margin-bottom: 28px;\r\n  border: 1px solid rgba(187, 247, 208, .18);\r\n  border-radius: 22px;\r\n  background: rgba(2, 6, 23, .38);\r\n  box-shadow: 0 18px 60px rgba(0,0,0,.22);\r\n}\r\n\r\n.brand-logo[_ngcontent-%COMP%] {\r\n  width: 52px;\r\n  height: 52px;\r\n  filter: drop-shadow(0 14px 26px rgba(16,185,129,.25));\r\n}\r\n\r\n.brand-text[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  gap: 2px;\r\n}\r\n\r\n.brand-name[_ngcontent-%COMP%] {\r\n  font-size: 18px;\r\n  font-weight: 950;\r\n  letter-spacing: -.03em;\r\n}\r\n\r\n.brand-subtitle[_ngcontent-%COMP%] {\r\n  color: var(--muted);\r\n  font-size: 12px;\r\n  font-weight: 800;\r\n  letter-spacing: .12em;\r\n  text-transform: uppercase;\r\n}\r\n\r\n.impact-hub[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  grid-template-columns: 1.25fr repeat(3, 1fr);\r\n  gap: 14px;\r\n  margin: 0 0 18px;\r\n}\r\n\r\n.impact-card[_ngcontent-%COMP%], \r\n.severity-map[_ngcontent-%COMP%] {\r\n  border: 1px solid var(--border);\r\n  border-radius: 28px;\r\n  background:\r\n    radial-gradient(circle at top left, rgba(52,211,153,.14), transparent 42%),\r\n    linear-gradient(145deg, rgba(255,255,255,.095), rgba(255,255,255,.04));\r\n  box-shadow: 0 22px 90px rgba(0,0,0,.22);\r\n  backdrop-filter: blur(18px);\r\n}\r\n\r\n.impact-card[_ngcontent-%COMP%] {\r\n  min-height: 156px;\r\n  padding: 20px;\r\n  display: flex;\r\n  flex-direction: column;\r\n  gap: 10px;\r\n}\r\n\r\n.impact-card[_ngcontent-%COMP%]   span[_ngcontent-%COMP%], \r\n.severity-row-head[_ngcontent-%COMP%]   span[_ngcontent-%COMP%] {\r\n  color: var(--muted);\r\n  font-size: 12px;\r\n  font-weight: 900;\r\n  letter-spacing: .12em;\r\n  text-transform: uppercase;\r\n}\r\n\r\n.impact-card[_ngcontent-%COMP%]   strong[_ngcontent-%COMP%] {\r\n  font-size: clamp(20px, 2vw, 30px);\r\n  letter-spacing: -.04em;\r\n  line-height: 1.05;\r\n}\r\n\r\n.impact-card[_ngcontent-%COMP%]   p[_ngcontent-%COMP%] {\r\n  margin: 0;\r\n  color: var(--muted);\r\n}\r\n\r\n.primary-impact[_ngcontent-%COMP%] {\r\n  border-color: rgba(52,211,153,.34);\r\n  background:\r\n    radial-gradient(circle at 22% 0%, rgba(52,211,153,.28), transparent 44%),\r\n    linear-gradient(145deg, rgba(16,185,129,.16), rgba(255,255,255,.055));\r\n}\r\n\r\n.scan-identity[_ngcontent-%COMP%]   strong[_ngcontent-%COMP%], \r\n.scan-identity[_ngcontent-%COMP%]   p[_ngcontent-%COMP%] {\r\n  overflow-wrap: anywhere;\r\n}\r\n\r\n.severity-map[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  grid-template-columns: .72fr 1.28fr;\r\n  gap: 22px;\r\n  align-items: center;\r\n  margin: 0 0 18px;\r\n  padding: 22px;\r\n}\r\n\r\n.severity-map-copy[_ngcontent-%COMP%]   h2[_ngcontent-%COMP%] {\r\n  margin-bottom: 8px;\r\n  font-size: clamp(26px, 3vw, 42px);\r\n  letter-spacing: -.06em;\r\n}\r\n\r\n.severity-map-copy[_ngcontent-%COMP%]   p[_ngcontent-%COMP%] {\r\n  margin: 0;\r\n  color: var(--muted);\r\n  line-height: 1.6;\r\n}\r\n\r\n.severity-bars[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  gap: 12px;\r\n}\r\n\r\n.severity-row[_ngcontent-%COMP%] {\r\n  display: grid;\r\n  gap: 8px;\r\n}\r\n\r\n.severity-row-head[_ngcontent-%COMP%] {\r\n  display: flex;\r\n  justify-content: space-between;\r\n  gap: 16px;\r\n}\r\n\r\n.severity-row-head[_ngcontent-%COMP%]   strong[_ngcontent-%COMP%] {\r\n  font-size: 13px;\r\n}\r\n\r\n.severity-track[_ngcontent-%COMP%] {\r\n  height: 12px;\r\n  border-radius: 999px;\r\n  background: rgba(255,255,255,.075);\r\n  border: 1px solid rgba(255,255,255,.08);\r\n  overflow: hidden;\r\n}\r\n\r\n.severity-track[_ngcontent-%COMP%]   i[_ngcontent-%COMP%] {\r\n  display: block;\r\n  height: 100%;\r\n  min-width: 4px;\r\n  border-radius: inherit;\r\n  background: linear-gradient(90deg, var(--brand), var(--brand-strong));\r\n  box-shadow: 0 0 26px rgba(52,211,153,.34);\r\n}\r\n\r\n.severity-row[data-severity=\"CRITICAL\"][_ngcontent-%COMP%]   .severity-track[_ngcontent-%COMP%]   i[_ngcontent-%COMP%] {\r\n  background: linear-gradient(90deg, #fb7185, #ef4444);\r\n  box-shadow: 0 0 26px rgba(251,113,133,.34);\r\n}\r\n\r\n.severity-row[data-severity=\"MAJOR\"][_ngcontent-%COMP%]   .severity-track[_ngcontent-%COMP%]   i[_ngcontent-%COMP%] {\r\n  background: linear-gradient(90deg, #fbbf24, #f97316);\r\n  box-shadow: 0 0 26px rgba(251,191,36,.28);\r\n}\r\n\r\n.severity-row[data-severity=\"MINOR\"][_ngcontent-%COMP%]   .severity-track[_ngcontent-%COMP%]   i[_ngcontent-%COMP%] {\r\n  background: linear-gradient(90deg, #a78bfa, #6366f1);\r\n  box-shadow: 0 0 26px rgba(167,139,250,.28);\r\n}\r\n\r\n.tabs[_ngcontent-%COMP%] {\r\n  position: sticky;\r\n  top: 12px;\r\n  z-index: 10;\r\n  backdrop-filter: blur(24px);\r\n  box-shadow: 0 18px 70px rgba(0,0,0,.22);\r\n}\r\n\r\n.finding[_ngcontent-%COMP%], \r\n.action[_ngcontent-%COMP%], \r\n.component[_ngcontent-%COMP%], \r\n.gate-card[_ngcontent-%COMP%], \r\n.area-card[_ngcontent-%COMP%], \r\n.decision-lane[_ngcontent-%COMP%] {\r\n  transition: transform .18s ease, border-color .18s ease, background .18s ease, box-shadow .18s ease;\r\n}\r\n\r\n.finding[_ngcontent-%COMP%]:hover, \r\n.action[_ngcontent-%COMP%]:hover, \r\n.component[_ngcontent-%COMP%]:hover, \r\n.gate-card[_ngcontent-%COMP%]:hover, \r\n.area-card[_ngcontent-%COMP%]:hover, \r\n.decision-lane[_ngcontent-%COMP%]:hover {\r\n  box-shadow: 0 18px 70px rgba(0,0,0,.18);\r\n}\r\n\r\n.primary[_ngcontent-%COMP%], \r\n.ghost[_ngcontent-%COMP%], \r\n.tabs[_ngcontent-%COMP%]   button[_ngcontent-%COMP%], \r\n.language-switch[_ngcontent-%COMP%]   button[_ngcontent-%COMP%], \r\n.mode-switch[_ngcontent-%COMP%]   button[_ngcontent-%COMP%], \r\n.radio-card[_ngcontent-%COMP%] {\r\n  transition: transform .16s ease, background .16s ease, border-color .16s ease, box-shadow .16s ease;\r\n}\r\n\r\n.primary[_ngcontent-%COMP%]:hover, \r\n.ghost[_ngcontent-%COMP%]:hover, \r\n.tabs[_ngcontent-%COMP%]   button[_ngcontent-%COMP%]:hover, \r\n.language-switch[_ngcontent-%COMP%]   button[_ngcontent-%COMP%]:hover, \r\n.mode-switch[_ngcontent-%COMP%]   button[_ngcontent-%COMP%]:hover, \r\n.radio-card[_ngcontent-%COMP%]:hover {\r\n  transform: translateY(-1px);\r\n}\r\n\r\n.primary[_ngcontent-%COMP%]:active, \r\n.ghost[_ngcontent-%COMP%]:active, \r\n.tabs[_ngcontent-%COMP%]   button[_ngcontent-%COMP%]:active, \r\n.language-switch[_ngcontent-%COMP%]   button[_ngcontent-%COMP%]:active, \r\n.mode-switch[_ngcontent-%COMP%]   button[_ngcontent-%COMP%]:active, \r\n.radio-card[_ngcontent-%COMP%]:active {\r\n  transform: translateY(0);\r\n}\r\n\r\n@media (max-width: 1180px) {\r\n  .impact-hub[_ngcontent-%COMP%], \r\n   .severity-map[_ngcontent-%COMP%] {\r\n    grid-template-columns: 1fr 1fr;\r\n  }\r\n\r\n  .primary-impact[_ngcontent-%COMP%] {\r\n    grid-column: 1 / -1;\r\n  }\r\n}\r\n\r\n@media (max-width: 760px) {\r\n  .impact-hub[_ngcontent-%COMP%], \r\n   .severity-map[_ngcontent-%COMP%] {\r\n    grid-template-columns: 1fr;\r\n  }\r\n\r\n  .main-score[_ngcontent-%COMP%] {\r\n    grid-column: auto;\r\n  }\r\n\r\n  .brand-lockup[_ngcontent-%COMP%] {\r\n    width: 100%;\r\n  }\r\n}\r\n\n.module-grid[_ngcontent-%COMP%] {\n  display: grid;\n  gap: 16px;\n  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));\n}\n\n.module-card[_ngcontent-%COMP%] {\n  border: 1px solid rgba(148, 163, 184, .18);\n  border-radius: 24px;\n  padding: 18px;\n  min-height: 150px;\n  background: linear-gradient(145deg, rgba(15, 23, 42, .72), rgba(15, 118, 110, .12));\n  box-shadow: inset 0 1px 0 rgba(255,255,255,.05);\n  transition: transform .18s ease, border-color .18s ease, box-shadow .18s ease;\n}\n\n.module-card[data-active=\"true\"][_ngcontent-%COMP%] {\n  border-color: rgba(52, 211, 153, .5);\n  background: linear-gradient(145deg, rgba(20, 83, 45, .42), rgba(15, 23, 42, .78));\n}\n\n.module-card[_ngcontent-%COMP%]:hover {\n  transform: translateY(-2px);\n  box-shadow: 0 20px 80px rgba(0, 0, 0, .2);\n}\n\n.module-card[_ngcontent-%COMP%]   small[_ngcontent-%COMP%] {\n  display: inline-flex;\n  margin-top: 12px;\n  color: var(--muted);\n  font-weight: 700;\n}\n\n\n\n.auto-analysis-box[_ngcontent-%COMP%] {\n  display: grid;\n  gap: 16px;\n  border: 1px solid rgba(52, 211, 153, .28);\n  border-radius: 24px;\n  padding: 18px;\n  background:\n    radial-gradient(circle at top left, rgba(52, 211, 153, .18), transparent 42%),\n    linear-gradient(145deg, rgba(15, 118, 110, .18), rgba(2, 6, 23, .18));\n}\n\n.auto-analysis-head[_ngcontent-%COMP%]   span[_ngcontent-%COMP%], \n.scan-ready-strip[_ngcontent-%COMP%]   span[_ngcontent-%COMP%] {\n  display: block;\n  color: var(--brand);\n  font-size: 12px;\n  font-weight: 900;\n  text-transform: uppercase;\n  letter-spacing: .12em;\n  margin-bottom: 6px;\n}\n\n.auto-analysis-head[_ngcontent-%COMP%]   strong[_ngcontent-%COMP%] {\n  display: block;\n  font-size: 22px;\n  letter-spacing: -.04em;\n  margin-bottom: 8px;\n}\n\n.auto-analysis-head[_ngcontent-%COMP%]   p[_ngcontent-%COMP%] {\n  color: var(--muted);\n  margin: 0;\n  line-height: 1.55;\n}\n\n.analysis-flow[_ngcontent-%COMP%] {\n  display: grid;\n  grid-template-columns: repeat(2, minmax(0, 1fr));\n  gap: 10px;\n}\n\n.analysis-flow[_ngcontent-%COMP%]    > div[_ngcontent-%COMP%] {\n  min-height: 92px;\n  border: 1px solid rgba(148, 163, 184, .20);\n  border-radius: 18px;\n  padding: 12px;\n  background: rgba(0, 0, 0, .18);\n}\n\n.analysis-flow[_ngcontent-%COMP%]   b[_ngcontent-%COMP%] {\n  display: inline-grid;\n  place-items: center;\n  width: 34px;\n  height: 34px;\n  border-radius: 12px;\n  color: #042117;\n  background: linear-gradient(135deg, var(--brand), #bbf7d0);\n  font-size: 13px;\n  margin-bottom: 10px;\n}\n\n.analysis-flow[_ngcontent-%COMP%]   span[_ngcontent-%COMP%] {\n  display: block;\n  color: var(--text);\n  font-size: 13px;\n  line-height: 1.45;\n}\n\n.scan-ready-strip[_ngcontent-%COMP%] {\n  display: grid;\n  gap: 5px;\n  border: 1px solid rgba(187, 247, 208, .22);\n  border-radius: 20px;\n  padding: 14px 16px;\n  background: rgba(52, 211, 153, .10);\n}\n\n.scan-ready-strip[_ngcontent-%COMP%]   strong[_ngcontent-%COMP%] {\n  font-size: 18px;\n}\n\n.scan-card[_ngcontent-%COMP%]   .primary[_ngcontent-%COMP%] {\n  min-height: 58px;\n  font-size: 16px;\n  letter-spacing: .02em;\n  box-shadow: 0 18px 42px rgba(16, 185, 129, .24);\n}\n\n.mode-switch[_ngcontent-%COMP%] {\n  display: grid;\n  grid-template-columns: repeat(3, minmax(0, 1fr));\n}\n\n.mode-switch[_ngcontent-%COMP%]   button[_ngcontent-%COMP%] {\n  min-height: 48px;\n  font-weight: 800;\n}\n\n.scan-card[_ngcontent-%COMP%]   input[type=\"text\"][_ngcontent-%COMP%], \n.scan-card[_ngcontent-%COMP%]   input[type=\"file\"][_ngcontent-%COMP%] {\n  min-height: 54px;\n  font-weight: 700;\n}\n\n@media (max-width: 520px) {\n  .analysis-flow[_ngcontent-%COMP%], \n   .mode-switch[_ngcontent-%COMP%] {\n    grid-template-columns: 1fr;\n  }\n}"] }); }
}
(() => { (typeof ngDevMode === "undefined" || ngDevMode) && i0.ɵsetClassMetadata(AppComponent, [{
        type: Component,
        args: [{ selector: 'app-root', standalone: true, imports: [CommonModule, FormsModule], template: "<div class=\"shell\">\n  <header class=\"hero\">\n    <div class=\"hero-copy\">\n      <div class=\"brand-lockup\" aria-label=\"Spring Guardian brand\">\n        <img class=\"brand-logo\" src=\"assets/spring-guardian-logo.svg\" [attr.alt]=\"t('logoAlt')\">\n        <div class=\"brand-text\">\n          <span class=\"brand-name\">Spring Guardian</span>\n          <span class=\"brand-subtitle\">{{ t('brandSubtitle') }}</span>\n        </div>\n      </div>\n\n      <div class=\"hero-topline\">\n        <p class=\"eyebrow\">{{ t('eyebrow') }}</p>\n        <div class=\"language-switch\" role=\"group\" [attr.aria-label]=\"t('language')\">\n          <span>{{ t('language') }}</span>\n          <button [class.active]=\"selectedLanguage() === 'it'\" type=\"button\" (click)=\"selectLanguage('it')\">{{ t('italian') }}</button>\n          <button [class.active]=\"selectedLanguage() === 'en'\" type=\"button\" (click)=\"selectLanguage('en')\">{{ t('english') }}</button>\n        </div>\n      </div>\n      <h1>{{ t('heroTitle') }}</h1>\n      <p class=\"hero-text\">{{ t('heroText') }}</p>\n    </div>\n\n    <section class=\"scan-card\">\n      <div class=\"mode-switch\" role=\"tablist\" [attr.aria-label]=\"t('startScan')\">\n        <button [class.active]=\"uploadMode() === 'zip'\" type=\"button\" (click)=\"selectMode('zip')\">{{ t('uploadZip') }}</button>\n        <button [class.active]=\"uploadMode() === 'folder'\" type=\"button\" (click)=\"selectMode('folder')\">{{ t('uploadFolder') }}</button>\n        <button [class.active]=\"uploadMode() === 'local'\" type=\"button\" (click)=\"selectMode('local')\">{{ t('backendPath') }}</button>\n      </div>\n\n      <div *ngIf=\"uploadMode() === 'zip'\" class=\"field\">\n        <label for=\"zipFile\">{{ t('zipProjectFile') }}</label>\n        <input id=\"zipFile\" type=\"file\" accept=\".zip\" (change)=\"onFileSelected($event)\">\n        <small *ngIf=\"selectedFile()\">{{ t('selected') }}: {{ selectedFile()?.name }}</small>\n        <small *ngIf=\"!selectedFile()\">{{ t('zipHelp') }}</small>\n      </div>\n\n      <div *ngIf=\"uploadMode() === 'folder'\" class=\"field\">\n        <label for=\"projectFolder\">{{ t('projectRootFolder') }}</label>\n        <input id=\"projectFolder\" type=\"file\" webkitdirectory directory multiple (change)=\"onFolderSelected($event)\">\n        <small *ngIf=\"selectedFolderFiles().length > 0\">{{ t('selectedFemale') }}: {{ folderFilesLabel() }}</small>\n        <small *ngIf=\"selectedFolderFiles().length === 0\">{{ t('folderHelp') }}</small>\n      </div>\n\n      <div *ngIf=\"uploadMode() === 'local'\" class=\"field\">\n        <label for=\"localPath\">{{ t('backendFolderPath') }}</label>\n        <input id=\"localPath\" type=\"text\" [(ngModel)]=\"localPath\" placeholder=\"C:\\progetti\\mio-progetto oppure /scan/mio-progetto\">\n        <small>{{ t('backendPathHelp') }}</small>\n      </div>\n\n      <div class=\"auto-analysis-box\">\n        <div class=\"auto-analysis-head\">\n          <span>{{ t('scanSettings') }}</span>\n          <strong>{{ t('automaticProfileTitle') }}</strong>\n          <p>{{ t('automaticProfileText') }}</p>\n        </div>\n        <div class=\"analysis-flow\" [attr.aria-label]=\"t('analysisStepsTitle')\">\n          <div><b>01</b><span>{{ t('analysisStepModules') }}</span></div>\n          <div><b>02</b><span>{{ t('analysisStepRules') }}</span></div>\n          <div><b>03</b><span>{{ t('analysisStepAlternatives') }}</span></div>\n          <div><b>04</b><span>{{ t('analysisStepProduction') }}</span></div>\n        </div>\n        <small>{{ t('noManualProfile') }}</small>\n      </div>\n\n      <div class=\"scan-ready-strip\">\n        <span>{{ t('projectType') }}</span>\n        <strong>{{ t('autoDetected') }}</strong>\n        <small>{{ t('profileHelp') }}</small>\n      </div>\n\n      <button class=\"primary\" type=\"button\" (click)=\"scan()\" [disabled]=\"loading()\">\n        {{ loading() ? t('scanRunning') : t('startScan') }}\n      </button>\n\n      <p *ngIf=\"error()\" class=\"error\">{{ error() }}</p>\n    </section>\n  </header>\n\n  <main *ngIf=\"report() as current; else emptyState\">\n    <section class=\"score-grid\">\n      <article class=\"score-card main-score\">\n        <span>{{ t('architectureScore') }}</span>\n        <strong [class]=\"scoreClass(current.architectureScore)\">{{ current.architectureScore }}</strong>\n        <p>{{ riskLabel(current.riskLevel) }} \u00B7 {{ current.releaseReadiness.label }}</p>\n      </article>\n\n      <article class=\"score-card\" *ngFor=\"let severity of severityOrder\">\n        <span>{{ severityLabel(severity) }}</span>\n        <strong>{{ severityCount(severity) }}</strong>\n        <p>{{ t('findings') }}</p>\n      </article>\n\n      <article class=\"score-card advisor-score\">\n        <span>{{ t('springAdvisor') }}</span>\n        <strong>{{ advisorCount(current) }}</strong>\n        <p>{{ t('advisorOpportunities') }}</p>\n      </article>\n\n      <article class=\"score-card\">\n        <span>{{ t('typeConfiguration') }}</span>\n        <strong>{{ typeOccurrenceCount(current, 'CONFIGURATION') }}</strong>\n        <p>{{ t('findings') }}</p>\n      </article>\n\n      <article class=\"score-card\">\n        <span>{{ t('typeDependencies') }}</span>\n        <strong>{{ typeOccurrenceCount(current, 'DEPENDENCIES') }}</strong>\n        <p>{{ t('findings') }}</p>\n      </article>\n\n      <article class=\"score-card\">\n        <span>{{ t('rulesExecuted') }}</span>\n        <strong>{{ current.rulesExecuted }}</strong>\n        <p>{{ current.scannedJavaFiles }} {{ t('javaFiles') }} \u00B7 {{ current.scannedPomFiles }} {{ t('pomFiles') }}</p>\n      </article>\n    </section>\n\n    <section class=\"impact-hub\" aria-label=\"Report impact dashboard\">\n      <article class=\"impact-card primary-impact\">\n        <span>{{ t('precisionMode') }}</span>\n        <strong>{{ t('highConfidence') }}</strong>\n        <p>{{ t('precisionModeText') }}</p>\n      </article>\n\n      <article class=\"impact-card\">\n        <span>{{ t('scopeOnly') }}</span>\n        <strong>{{ detectedScopeLabel(current) }}</strong>\n        <p>{{ t('scopeOnlyText') }}</p>\n      </article>\n\n      <article class=\"impact-card\">\n        <span>{{ t('evidenceFirst') }}</span>\n        <strong>{{ current.findings.length }} rules \u00B7 {{ totalFindingOccurrences() }} {{ t('occurrences') }}</strong>\n        <p>{{ t('evidenceFirstText') }}</p>\n      </article>\n\n      <article class=\"impact-card scan-identity\">\n        <span>{{ t('projectIdentity') }}</span>\n        <strong>{{ current.projectName }}</strong>\n        <p *ngIf=\"current.projectRootPath\">{{ shortPath(current.projectRootPath) }}</p>\n        <p *ngIf=\"!current.projectRootPath && currentScanSource()\">{{ shortPath(currentScanSource()) }}</p>\n      </article>\n    </section>\n\n    <section class=\"severity-map\" aria-label=\"Severity distribution\">\n      <div class=\"severity-map-copy\">\n        <h2>{{ t('impactVisualTitle') }}</h2>\n        <p>{{ t('impactVisualText') }}</p>\n      </div>\n      <div class=\"severity-bars\">\n        <div class=\"severity-row\" *ngFor=\"let severity of severityOrder\" [attr.data-severity]=\"severity\">\n          <div class=\"severity-row-head\">\n            <span>{{ severityLabel(severity) }}</span>\n            <strong>{{ severityCount(severity) }} \u00B7 {{ severityPercent(current, severity) }}%</strong>\n          </div>\n          <div class=\"severity-track\">\n            <i [style.width.%]=\"severityPercent(current, severity)\"></i>\n          </div>\n        </div>\n      </div>\n    </section>\n\n    <section class=\"decision-board\" aria-label=\"Priorit\u00E0 di intervento\">\n      <div class=\"decision-board-title\">\n        <div>\n          <h2>{{ t('decisionBoard') }}</h2>\n          <p>{{ t('decisionBoardText') }}</p>\n        </div>\n      </div>\n      <div class=\"decision-lanes\">\n        <article class=\"decision-lane\" *ngFor=\"let lane of decisionLanes()\" [attr.data-lane]=\"lane.lane\">\n          <span>{{ lane.title }}</span>\n          <strong>{{ lane.count }}</strong>\n          <p>{{ lane.text }}</p>\n          <button type=\"button\" class=\"ghost\" (click)=\"focusDecisionLane(lane.lane)\">{{ t('openLane') }}</button>\n        </article>\n      </div>\n    </section>\n\n\n    <nav class=\"tabs\" aria-label=\"Report sections\">\n      <button type=\"button\" [class.active]=\"activeTab() === 'overview'\" (click)=\"selectTab('overview')\">{{ t('overview') }}</button>\n      <button type=\"button\" [class.active]=\"activeTab() === 'modules'\" (click)=\"selectTab('modules')\">{{ t('springModules') }}</button>\n      <button type=\"button\" [class.active]=\"activeTab() === 'gates'\" (click)=\"selectTab('gates')\">{{ t('gates') }}</button>\n      <button type=\"button\" [class.active]=\"activeTab() === 'findings'\" (click)=\"selectTab('findings')\">{{ t('springArchitecture') }}</button>\n      <button type=\"button\" [class.active]=\"activeTab() === 'advisor'\" (click)=\"selectTab('advisor')\">{{ t('springAdvisor') }}</button>\n      <button type=\"button\" [class.active]=\"activeTab() === 'production'\" (click)=\"selectTab('production')\">{{ t('productionRules') }}</button>\n      <button type=\"button\" [class.active]=\"activeTab() === 'suggestions'\" (click)=\"selectTab('suggestions')\">{{ t('suggestionsToVerify') }}</button>\n      <button type=\"button\" [class.active]=\"activeTab() === 'actions'\" (click)=\"selectTab('actions')\">{{ t('actions') }}</button>\n      <button type=\"button\" [class.active]=\"activeTab() === 'json'\" (click)=\"selectTab('json')\">{{ t('technicalJson') }}</button>\n    </nav>\n\n    <section *ngIf=\"activeTab() === 'overview'\" class=\"panel-grid\">\n      <article class=\"panel wide readiness\" [attr.data-status]=\"current.releaseReadiness.status\">\n        <div class=\"panel-title\">\n          <h2>{{ t('releaseReadiness') }}</h2>\n          <span>{{ current.releaseReadiness.label }}</span>\n        </div>\n        <p>{{ current.releaseReadiness.explanation }}</p>\n        <div class=\"release-grid\">\n          <div>\n            <h3>{{ t('blockers') }}</h3>\n            <p *ngIf=\"current.releaseReadiness.blockers.length === 0\">{{ t('noBlockers') }}</p>\n            <ul>\n              <li *ngFor=\"let blocker of current.releaseReadiness.blockers\">{{ blocker }}</li>\n            </ul>\n          </div>\n          <div>\n            <h3>{{ t('warnings') }}</h3>\n            <p *ngIf=\"current.releaseReadiness.warnings.length === 0\">{{ t('noWarnings') }}</p>\n            <ul>\n              <li *ngFor=\"let warning of current.releaseReadiness.warnings\">{{ warning }}</li>\n            </ul>\n          </div>\n        </div>\n      </article>\n\n      <article class=\"panel wide\">\n        <h2>{{ t('executiveSummary') }}</h2>\n        <p>{{ current.summary.executiveSummary }}</p>\n        <div class=\"meta-row\">\n          <span>{{ t('project') }}: <strong>{{ current.projectName }}</strong></span>\n          <span *ngIf=\"currentScanSource()\">{{ t('requestedSource') }}: <strong>{{ currentScanSource() }}</strong></span>\n          <span *ngIf=\"current.projectRootPath\">{{ t('scannedRootPath') }}: <strong>{{ current.projectRootPath }}</strong></span>\n          <span>{{ t('scan') }}: <strong>{{ current.scannedAt | date:'medium' }}</strong></span>\n          <span>{{ t('selectedProfile') }}: <strong>{{ detectedScopeLabel(current) }} \u00B7 {{ architectureStyleLabel(current.profile.architectureStyle) }}</strong></span>\n        </div>\n      </article>\n\n\n      <article class=\"panel wide\">\n        <h2>{{ t('findingType') }}</h2>\n        <div class=\"area-grid\">\n          <article class=\"area-card\" *ngFor=\"let type of current.findingsByType\">\n            <div class=\"category-head\">\n              <strong>{{ type.category }}</strong>\n              <span>{{ type.findings }} {{ t('findings') }}</span>\n            </div>\n            <p>{{ type.explanation }}</p>\n            <div class=\"area-metrics\">\n              <span>{{ type.criticalFindings }} {{ t('critical').toLowerCase() }}</span>\n              <span>{{ type.majorFindings }} {{ t('major').toLowerCase() }}</span>\n              <span>{{ type.minorFindings }} {{ t('minor').toLowerCase() }}</span>\n            </div>\n          </article>\n        </div>\n      </article>\n\n      <article class=\"panel\">\n        <h2>{{ t('detectedStack') }}</h2>\n        <div class=\"chip-list\">\n          <span *ngFor=\"let item of capabilityItems(current)\">{{ item }}</span>\n        </div>\n        <p>{{ t('detectedStyles') }}: <strong>{{ current.capabilities.detectedArchitecturalStyles.join(', ') }}</strong></p>\n      </article>\n\n      <article class=\"panel\">\n        <h2>{{ t('howToRead') }}</h2>\n        <p>{{ current.explanation.scoreMeaning }}</p>\n        <p>{{ current.explanation.severityMeaning }}</p>\n        <ul>\n          <li *ngFor=\"let step of current.explanation.nextSteps\">{{ step }}</li>\n        </ul>\n      </article>\n    </section>\n\n    <section *ngIf=\"activeTab() === 'modules'\" class=\"panel-grid\">\n      <article class=\"panel wide\">\n        <div class=\"panel-title\">\n          <div>\n            <h2>{{ t('springModules') }}</h2>\n            <p>{{ t('springModulesSubtitle') }}</p>\n          </div>\n          <span>{{ moduleCards().length }} {{ t('springModules').toLowerCase() }}</span>\n        </div>\n        <div class=\"module-grid\">\n          <article class=\"module-card\" *ngFor=\"let module of moduleCards()\" [attr.data-active]=\"module.active\">\n            <div class=\"category-head\">\n              <strong>{{ module.name }}</strong>\n              <span>{{ module.status }}</span>\n            </div>\n            <p>{{ module.description }}</p>\n            <small>{{ module.evidence }}</small>\n          </article>\n        </div>\n      </article>\n\n      <article class=\"panel wide\">\n        <h2>{{ t('springModules') }} \u00B7 {{ t('springAdvisor') }}</h2>\n        <p>{{ t('advisorSubtitle') }}</p>\n        <div class=\"area-grid\">\n          <article class=\"area-card\" *ngFor=\"let finding of current.findings\" [hidden]=\"finding.findingType !== 'SPRING_CAPABILITY_GAP'\">\n            <div class=\"category-head\">\n              <strong>{{ finding.title }}</strong>\n              <span>{{ t('confidence') }}: {{ confidenceLabel(finding) }}</span>\n            </div>\n            <p>{{ finding.guidance.riskImpact }}</p>\n            <strong>{{ finding.guidance.recommendedApproach }}</strong>\n          </article>\n        </div>\n      </article>\n    </section>\n\n    <section *ngIf=\"activeTab() === 'gates'\" class=\"panel-grid\">\n      <article class=\"panel wide\">\n        <div class=\"panel-title\">\n          <h2>{{ t('gateStatus') }}</h2>\n          <span>{{ current.qualityGates.length }} {{ t('gates').toLowerCase() }}</span>\n        </div>\n        <div class=\"gate-grid\">\n          <article class=\"gate-card\" *ngFor=\"let gate of current.qualityGates\" [attr.data-status]=\"gate.status\">\n            <div class=\"category-head\">\n              <strong>{{ gate.name }}</strong>\n              <span>{{ gateStatusLabel(gate.status) }}</span>\n            </div>\n            <p>{{ gate.explanation }}</p>\n            <small>{{ gate.failingFindings }} {{ t('failingFindings') }}</small>\n          </article>\n        </div>\n      </article>\n\n      <article class=\"panel wide\">\n        <h2>{{ t('impactedAreas') }}</h2>\n        <div class=\"area-grid\">\n          <article class=\"area-card\" *ngFor=\"let area of current.architectureAreas\" [attr.data-status]=\"area.readinessStatus\">\n            <div class=\"category-head\">\n              <strong>{{ area.name }}</strong>\n              <span>{{ gateStatusLabel(area.readinessStatus) }}</span>\n            </div>\n            <p>{{ area.description }}</p>\n            <div class=\"area-metrics\">\n              <span>{{ area.findings }} {{ t('findings') }}</span>\n              <span>{{ area.criticalFindings }} {{ t('critical').toLowerCase() }}</span>\n              <span>{{ area.majorFindings }} {{ t('major').toLowerCase() }}</span>\n            </div>\n          </article>\n        </div>\n      </article>\n    </section>\n\n    <section *ngIf=\"activeTab() === 'findings'\" class=\"panel\">\n      <div class=\"toolbar\">\n        <input type=\"search\" [(ngModel)]=\"search\" (ngModelChange)=\"touchFilters()\" [placeholder]=\"t('searchPlaceholder')\">\n        <select [(ngModel)]=\"severityFilter\" (ngModelChange)=\"touchFilters()\" aria-label=\"Severity filter\">\n          <option value=\"ALL\">{{ t('allSeverities') }}</option>\n          <option *ngFor=\"let severity of severityOrder\" [value]=\"severity\">{{ severityLabel(severity) }}</option>\n        </select>\n        <select [(ngModel)]=\"categoryFilter\" (ngModelChange)=\"touchFilters()\" aria-label=\"Category filter\">\n          <option value=\"ALL\">{{ t('allAreas') }}</option>\n          <option *ngFor=\"let category of categories()\" [value]=\"category\">{{ category }}</option>\n        </select>\n        <select [(ngModel)]=\"typeFilter\" (ngModelChange)=\"touchFilters()\" [attr.aria-label]=\"t('findingType')\">\n          <option value=\"ALL\">{{ t('allTypes') }}</option>\n          <option *ngFor=\"let type of findingTypes()\" [value]=\"type\">{{ findingTypeLabel(type) }}</option>\n        </select>\n        <button type=\"button\" class=\"ghost\" (click)=\"resetFilters()\">{{ t('clearFilters') }}</button>\n      </div>\n\n      <div class=\"problem-area-strip\" *ngIf=\"problemTypeSummaries().length > 0\">\n        <div class=\"strip-title\">{{ t('problemAreasTitle') }}</div>\n        <button\n          type=\"button\"\n          class=\"type-pill\"\n          *ngFor=\"let type of problemTypeSummaries()\"\n          [class.active]=\"typeFilter === type.type\"\n          (click)=\"setTypeFilter(type.type)\">\n          <span>{{ type.label }}</span>\n          <strong>{{ type.occurrences }}</strong>\n        </button>\n      </div>\n\n      <div class=\"active-filter\" *ngIf=\"activeDecisionLane()\">\n        {{ t('focusedLane') }}: <strong>{{ severityFilter === 'ALL' ? activeDecisionLane() : severityLabel(severityFilter) }}</strong>\n      </div>\n\n      <div class=\"finding-list grouped\">\n        <section class=\"problem-group\" *ngFor=\"let group of problemGroups()\">\n          <div class=\"problem-group-head\">\n            <div>\n              <span>{{ group.label }}</span>\n              <h3>{{ group.occurrences }} {{ t('findings') }}</h3>\n            </div>\n          </div>\n\n          <article class=\"finding\" *ngFor=\"let finding of group.findings; trackBy: trackFinding\">\n            <div class=\"finding-header\">\n              <div>\n                <span class=\"badge\" [attr.data-severity]=\"finding.severity\">{{ severityLabel(finding.severity) }}</span>\n                <span class=\"category\">{{ finding.category }}</span>\n                <span class=\"rule-code\">{{ t('technicalCode') }} {{ ruleCode(finding.ruleId) }}</span>\n              </div>\n              <strong>{{ occurrenceLabel(finding.occurrences) }}</strong>\n            </div>\n\n            <h3>{{ finding.title }}</h3>\n\n            <div class=\"finding-guidance action-oriented\">\n              <div class=\"guidance-item detected\">\n                <span>{{ t('detectedProblem') }}</span>\n                <p>{{ finding.guidance.detectedProblem }}</p>\n              </div>\n              <div class=\"guidance-item risk\">\n                <span>{{ t('riskImpact') }}</span>\n                <p>{{ finding.guidance.riskImpact }}</p>\n              </div>\n              <div class=\"guidance-item solution\">\n                <span>{{ t('recommendedFix') }}</span>\n                <p>{{ finding.guidance.recommendedApproach }}</p>\n              </div>\n            </div>\n\n            <a *ngIf=\"finding.guidance.documentationUrl\" class=\"doc-link\" [href]=\"finding.guidance.documentationUrl\" target=\"_blank\" rel=\"noopener\">\n              {{ t('officialDocs') }}\n            </a>\n\n            <div class=\"before-after compact\" *ngIf=\"false\">\n              <div *ngIf=\"finding.guidance.beforeExample\">\n                <h4>{{ t('currentFinding') }}</h4>\n                <pre class=\"code-evidence\"><code>{{ finding.guidance.beforeExample }}</code></pre>\n              </div>\n              <div *ngIf=\"finding.guidance.afterExample\">\n                <h4>{{ t('expectedImplementation') }}</h4>\n                <pre class=\"code-evidence\"><code>{{ finding.guidance.afterExample }}</code></pre>\n              </div>\n            </div>\n\n            <details>\n              <summary>{{ t('involvedComponents') }} ({{ finding.affectedComponents.length }})</summary>\n              <p class=\"json-note\" *ngIf=\"finding.affectedComponents.length > 12\">{{ t('showingFirstComponents') }}</p>\n              <div class=\"component-list\">\n                <div class=\"component\" *ngFor=\"let component of visibleComponents(finding)\">\n                  <strong>{{ componentTitle(component) }}</strong>\n                  <span>{{ componentTypeLabel(component.type) }}</span>\n                  <span>{{ component.filePath }}<ng-container *ngIf=\"component.line\">:{{ component.line }}</ng-container></span>\n                  <p *ngIf=\"component.evidence\"><b>{{ t('technicalEvidence') }}:</b> {{ component.evidence }}</p>\n                  <pre *ngIf=\"component.codeSnippet\" class=\"code-evidence\"><code>{{ component.codeSnippet }}</code></pre>\n                </div>\n              </div>\n              <p class=\"json-note\" *ngIf=\"remainingComponents(finding) > 0\">+{{ remainingComponents(finding) }} {{ t('moreComponents') }}</p>\n            </details>\n          </article>\n        </section>\n      </div>\n    </section>\n\n    <section *ngIf=\"activeTab() === 'advisor'\" class=\"panel advisor-panel\">\n      <div class=\"panel-title\">\n        <div>\n          <h2>{{ t('springAdvisor') }}</h2>\n          <p>{{ t('advisorSubtitle') }}</p>\n        </div>\n        <span>{{ advisorCount(current) }} {{ t('advisorOpportunities') }}</span>\n      </div>\n\n      <div *ngIf=\"springAdvisorFindings().length === 0\" class=\"empty inline-empty\">\n        <h2>{{ t('advisorEmptyTitle') }}</h2>\n        <p>{{ t('advisorEmptyText') }}</p>\n      </div>\n\n      <div class=\"advisor-group-list\" *ngIf=\"springAdvisorFindings().length > 0\">\n        <section class=\"advisor-group\" *ngFor=\"let group of advisorGroups()\">\n          <div class=\"advisor-group-title\">\n            <h3>{{ group.area }}</h3>\n            <span>{{ group.occurrences }} {{ t('advisorOpportunities') }}</span>\n          </div>\n\n          <article class=\"finding advisor-finding\" *ngFor=\"let finding of group.findings; trackBy: trackFinding\">\n            <div class=\"finding-header\">\n              <div>\n                <span class=\"badge\" [attr.data-severity]=\"finding.severity\">{{ severityLabel(finding.severity) }}</span>\n                <span class=\"category\">{{ advisorArea(finding) }}</span>\n                <span class=\"rule-code\">{{ t('technicalCode') }} {{ ruleCode(finding.ruleId) }}</span>\n              </div>\n              <strong>{{ occurrenceLabel(finding.occurrences) }}</strong>\n            </div>\n            <h3>{{ finding.title }}</h3>\n\n            <div class=\"advisor-flow\">\n              <div>\n                <span>{{ t('detectedProblem') }}</span>\n                <p>{{ finding.guidance.detectedProblem }}</p>\n              </div>\n              <div>\n                <span>{{ t('springAlternativeToUse') }}</span>\n                <p><strong>{{ finding.guidance.springAlternative }}</strong></p>\n              </div>\n              <div>\n                <span>{{ t('riskImpact') }}</span>\n                <p>{{ finding.guidance.riskImpact }}</p>\n              </div>\n              <div>\n                <span>{{ t('recommendedFix') }}</span>\n                <p>{{ finding.guidance.recommendedApproach }}</p>\n              </div>\n            </div>\n\n            <a *ngIf=\"finding.guidance.documentationUrl\" class=\"doc-link\" [href]=\"finding.guidance.documentationUrl\" target=\"_blank\" rel=\"noopener\">\n              {{ t('officialDocs') }}\n            </a>\n\n            <div class=\"real-evidence\" *ngIf=\"firstComponent(finding) as component\">\n              <h4>{{ t('realEvidence') }}</h4>\n              <p><strong>{{ componentTitle(component) }}</strong> \u00B7 {{ component.filePath }}<ng-container *ngIf=\"component.line\">:{{ component.line }}</ng-container></p>\n              <p *ngIf=\"component.evidence\"><b>{{ t('technicalEvidence') }}:</b> {{ component.evidence }}</p>\n              <pre *ngIf=\"component.codeSnippet\" class=\"code-evidence\"><code>{{ component.codeSnippet }}</code></pre>\n              <p class=\"json-note\">{{ t('examplesHidden') }}</p>\n            </div>\n\n\n            <div class=\"before-after\" *ngIf=\"false\">\n              <div *ngIf=\"finding.guidance.beforeExample\">\n                <h4>{{ t('beforeExample') }}</h4>\n                <pre class=\"code-evidence\"><code>{{ finding.guidance.beforeExample }}</code></pre>\n              </div>\n              <div *ngIf=\"finding.guidance.afterExample\">\n                <h4>{{ t('afterExample') }}</h4>\n                <pre class=\"code-evidence\"><code>{{ finding.guidance.afterExample }}</code></pre>\n              </div>\n            </div>\n\n            <details>\n              <summary>{{ t('involvedComponents') }} ({{ finding.affectedComponents.length }})</summary>\n              <div class=\"component-list\">\n                <div class=\"component\" *ngFor=\"let component of finding.affectedComponents\">\n                  <strong>{{ componentTitle(component) }}</strong>\n                  <span>{{ componentTypeLabel(component.type) }}</span>\n                  <span>{{ component.filePath }}<ng-container *ngIf=\"component.line\">:{{ component.line }}</ng-container></span>\n                  <p *ngIf=\"component.evidence\"><b>{{ t('technicalEvidence') }}:</b> {{ component.evidence }}</p>\n                  <pre *ngIf=\"component.codeSnippet\" class=\"code-evidence\"><code>{{ component.codeSnippet }}</code></pre>\n                </div>\n              </div>\n            </details>\n          </article>\n        </section>\n      </div>\n    </section>\n\n    <section *ngIf=\"activeTab() === 'production'\" class=\"panel advisor-panel\">\n      <div class=\"panel-title\">\n        <div>\n          <h2>{{ t('productionRules') }}</h2>\n          <p>{{ t('productionRulesSubtitle') }}</p>\n        </div>\n        <span>{{ productionFindings().length }} {{ t('findings') }}</span>\n      </div>\n      <div class=\"finding-list grouped\">\n        <article class=\"finding\" *ngFor=\"let finding of productionFindings(); trackBy: trackFinding\">\n          <div class=\"finding-header\">\n            <div>\n              <span class=\"badge\" [attr.data-severity]=\"finding.severity\">{{ severityLabel(finding.severity) }}</span>\n              <span class=\"category\">{{ finding.category }}</span>\n              <span class=\"rule-code\">{{ t('technicalCode') }} {{ ruleCode(finding.ruleId) }}</span>\n            </div>\n            <strong>{{ occurrenceLabel(finding.occurrences) }}</strong>\n          </div>\n          <h3>{{ finding.title }}</h3>\n          <div class=\"guidance-grid\">\n            <div><span>{{ t('riskImpact') }}</span><p>{{ finding.guidance.riskImpact }}</p></div>\n            <div><span>{{ t('recommendedFix') }}</span><p>{{ finding.guidance.recommendedApproach }}</p></div>\n          </div>\n          <div class=\"real-evidence\" *ngIf=\"firstComponent(finding) as component\">\n            <h4>{{ t('realEvidence') }}</h4>\n            <p><strong>{{ componentTitle(component) }}</strong> \u00B7 {{ component.filePath }}<ng-container *ngIf=\"component.line\">:{{ component.line }}</ng-container></p>\n            <p *ngIf=\"component.evidence\"><b>{{ t('technicalEvidence') }}:</b> {{ component.evidence }}</p>\n            <pre *ngIf=\"component.codeSnippet\" class=\"code-evidence\"><code>{{ component.codeSnippet }}</code></pre>\n          </div>\n        </article>\n      </div>\n    </section>\n\n    <section *ngIf=\"activeTab() === 'suggestions'\" class=\"panel advisor-panel\">\n      <div class=\"panel-title\">\n        <div>\n          <h2>{{ t('suggestionsToVerify') }}</h2>\n          <p>{{ t('suggestionsSubtitle') }}</p>\n        </div>\n        <span>{{ suggestionFindings().length }} {{ t('findings') }}</span>\n      </div>\n      <div class=\"finding-list grouped\">\n        <article class=\"finding\" *ngFor=\"let finding of suggestionFindings(); trackBy: trackFinding\">\n          <div class=\"finding-header\">\n            <div>\n              <span class=\"badge\" [attr.data-severity]=\"finding.severity\">{{ severityLabel(finding.severity) }}</span>\n              <span class=\"category\">{{ finding.category }}</span>\n              <span class=\"rule-code\">{{ t('confidence') }}: {{ confidenceLabel(finding) }}</span>\n            </div>\n            <strong>{{ occurrenceLabel(finding.occurrences) }}</strong>\n          </div>\n          <h3>{{ finding.title }}</h3>\n          <p>{{ finding.whyItMatters }}</p>\n          <strong>{{ finding.suggestedFix }}</strong>\n        </article>\n      </div>\n    </section>\n\n    <section *ngIf=\"activeTab() === 'actions'\" class=\"panel\">\n      <div class=\"panel-title\">\n        <h2>{{ t('recommendedActions') }}</h2>\n        <span>{{ current.recommendedActions.length }} {{ t('priorities') }}</span>\n      </div>\n      <div class=\"action-list\">\n        <article class=\"action\" *ngFor=\"let action of current.recommendedActions\">\n          <span class=\"priority\">#{{ action.priority }}</span>\n          <div>\n            <div class=\"finding-header compact\">\n              <span class=\"badge\" [attr.data-severity]=\"action.severity\">{{ severityLabel(action.severity) }}</span>\n              <span class=\"rule-code\">{{ ruleCode(action.ruleId) }}</span>\n              <span>{{ action.location }}</span>\n            </div>\n            <h3>{{ action.title }}</h3>\n            <p>{{ action.reason }}</p>\n            <strong>{{ action.action }}</strong>\n          </div>\n        </article>\n      </div>\n    </section>\n\n    <section *ngIf=\"activeTab() === 'json'\" class=\"panel\">\n      <div class=\"panel-title\">\n        <h2>{{ t('technicalJson') }}</h2>\n        <button type=\"button\" class=\"ghost\" (click)=\"exportJson()\">{{ t('exportJson') }}</button>\n      </div>\n      <p class=\"json-note\">{{ t('jsonNote') }}</p>\n      <pre>{{ rawJson() }}</pre>\n    </section>\n  </main>\n\n  <ng-template #emptyState>\n    <section class=\"empty\">\n      <img class=\"empty-logo\" src=\"assets/spring-guardian-logo.svg\" [attr.alt]=\"t('logoAlt')\">\n      <h2>{{ t('emptyTitle') }}</h2>\n      <p>{{ t('emptyText') }}</p>\n    </section>\n  </ng-template>\n</div>\n", styles: [".shell {\r\n  width: min(1440px, calc(100% - 40px));\r\n  margin: 0 auto;\r\n  padding: 32px 0 56px;\r\n}\r\n\r\n.hero {\r\n  display: grid;\r\n  grid-template-columns: minmax(0, 1fr) 460px;\r\n  gap: 28px;\r\n  align-items: stretch;\r\n  margin-bottom: 28px;\r\n}\r\n\r\n.hero-topline {\r\n  display: flex;\r\n  align-items: center;\r\n  justify-content: space-between;\r\n  gap: 16px;\r\n  flex-wrap: wrap;\r\n}\r\n\r\n.eyebrow {\r\n  margin: 0 0 10px;\r\n  color: var(--brand);\r\n  font-weight: 800;\r\n  letter-spacing: .14em;\r\n  text-transform: uppercase;\r\n}\r\n\r\nh1 {\r\n  margin: 0;\r\n  font-size: clamp(44px, 8vw, 92px);\r\n  line-height: .9;\r\n  letter-spacing: -0.08em;\r\n}\r\n\r\nh2, h3, h4, p { margin-top: 0; }\r\n\r\n.hero-text {\r\n  max-width: 780px;\r\n  color: var(--muted);\r\n  font-size: 19px;\r\n  line-height: 1.7;\r\n  margin: 22px 0 0;\r\n}\r\n\r\n.scan-card,\r\n.panel,\r\n.score-card,\r\n.empty {\r\n  border: 1px solid var(--border);\r\n  background: linear-gradient(145deg, rgba(255, 255, 255, .09), rgba(255, 255, 255, .045));\r\n  box-shadow: 0 22px 90px rgba(0, 0, 0, .26);\r\n  backdrop-filter: blur(18px);\r\n  border-radius: 28px;\r\n}\r\n\r\n.scan-card {\r\n  padding: 22px;\r\n  display: flex;\r\n  flex-direction: column;\r\n  gap: 18px;\r\n}\r\n\r\n.language-switch,\r\n.mode-switch,\r\n.tabs {\r\n  display: flex;\r\n  gap: 8px;\r\n  padding: 6px;\r\n  border-radius: 18px;\r\n  background: rgba(255,255,255,.06);\r\n  border: 1px solid var(--border);\r\n}\r\n\r\n.language-switch button,\r\n.mode-switch button,\r\n.tabs button,\r\n.ghost {\r\n  color: var(--text);\r\n  background: transparent;\r\n  border: 0;\r\n  border-radius: 14px;\r\n  padding: 10px 12px;\r\n}\r\n\r\n.language-switch button.active,\r\n.mode-switch button.active,\r\n.tabs button.active {\r\n  background: rgba(52, 211, 153, .18);\r\n  color: #bbf7d0;\r\n  box-shadow: inset 0 0 0 1px rgba(187, 247, 208, .22);\r\n}\r\n\r\n.language-switch {\r\n  align-items: center;\r\n}\r\n\r\n.language-switch span {\r\n  color: var(--muted);\r\n  font-size: 12px;\r\n  font-weight: 800;\r\n  text-transform: uppercase;\r\n  letter-spacing: .08em;\r\n  padding: 0 6px;\r\n}\r\n\r\n.field {\r\n  display: grid;\r\n  gap: 8px;\r\n}\r\n\r\nlabel {\r\n  color: var(--muted);\r\n  font-size: 13px;\r\n  font-weight: 700;\r\n  text-transform: uppercase;\r\n  letter-spacing: .08em;\r\n}\r\n\r\ninput,\r\nselect {\r\n  width: 100%;\r\n  border: 1px solid var(--border);\r\n  color: var(--text);\r\n  background: rgba(0, 0, 0, .22);\r\n  border-radius: 16px;\r\n  padding: 13px 14px;\r\n  outline: none;\r\n}\r\n\r\nsmall { color: var(--muted); line-height: 1.45; }\r\n\r\n.primary {\r\n  border: 0;\r\n  border-radius: 18px;\r\n  background: linear-gradient(135deg, var(--brand), var(--brand-strong));\r\n  color: #062018;\r\n  font-weight: 900;\r\n  padding: 14px 18px;\r\n}\r\n\r\n.primary:disabled { opacity: .65; cursor: progress; }\r\n.error { color: #fecdd3; margin: 0; }\r\n\r\n.score-grid {\r\n  display: grid;\r\n  grid-template-columns: 1.4fr repeat(6, 1fr);\r\n  gap: 14px;\r\n  margin-bottom: 18px;\r\n}\r\n\r\n.score-card {\r\n  padding: 18px;\r\n  min-height: 126px;\r\n}\r\n\r\n.score-card span,\r\n.meta-row span,\r\n.panel-title span,\r\n.category,\r\n.rule-code,\r\n.component span,\r\n.finding-header strong {\r\n  color: var(--muted);\r\n}\r\n\r\n.score-card strong {\r\n  display: block;\r\n  font-size: 36px;\r\n  margin: 8px 0;\r\n}\r\n\r\n.main-score strong { font-size: 56px; }\r\n.score-good { color: var(--brand); }\r\n.score-warning { color: var(--warning); }\r\n.score-danger { color: var(--danger); }\r\n\r\n.tabs { margin-bottom: 18px; width: fit-content; }\r\n\r\n.panel-grid {\r\n  display: grid;\r\n  grid-template-columns: 1.2fr .8fr;\r\n  gap: 18px;\r\n}\r\n\r\n.panel {\r\n  padding: 24px;\r\n}\r\n\r\n.wide { grid-column: 1 / -1; }\r\n\r\n.meta-row,\r\n.panel-title,\r\n.finding-header,\r\n.toolbar {\r\n  display: flex;\r\n  align-items: center;\r\n  justify-content: space-between;\r\n  gap: 12px;\r\n  flex-wrap: wrap;\r\n}\r\n\r\n.category-list,\r\n.finding-list,\r\n.action-list,\r\n.component-list {\r\n  display: grid;\r\n  gap: 14px;\r\n}\r\n\r\n.category-head {\r\n  display: flex;\r\n  justify-content: space-between;\r\n  gap: 12px;\r\n}\r\n\r\n.category-list p,\r\n.finding p,\r\n.action p,\r\n.component p,\r\n.panel p,\r\nli {\r\n  color: var(--muted);\r\n  line-height: 1.6;\r\n}\r\n\r\n.toolbar {\r\n  margin-bottom: 20px;\r\n  align-items: stretch;\r\n}\r\n\r\n.toolbar input { min-width: min(420px, 100%); flex: 1; }\r\n.toolbar select { width: auto; min-width: 180px; }\r\n.ghost { border: 1px solid var(--border); background: rgba(255,255,255,.05); }\r\n\r\n.finding,\r\n.action,\r\n.component {\r\n  border: 1px solid var(--border);\r\n  background: rgba(0,0,0,.16);\r\n  border-radius: 22px;\r\n  padding: 18px;\r\n}\r\n\r\n.finding h3,\r\n.action h3 { margin: 14px 0 8px; }\r\n\r\n.badge {\r\n  display: inline-flex;\r\n  border-radius: 999px;\r\n  padding: 5px 9px;\r\n  font-size: 12px;\r\n  font-weight: 900;\r\n  margin-right: 8px;\r\n}\r\n\r\n.badge[data-severity=\"CRITICAL\"] { background: rgba(251, 113, 133, .18); color: #fecdd3; }\r\n.badge[data-severity=\"MAJOR\"] { background: rgba(251, 191, 36, .18); color: #fde68a; }\r\n.badge[data-severity=\"MINOR\"] { background: rgba(167, 139, 250, .18); color: #ddd6fe; }\r\n.badge[data-severity=\"INFO\"] { background: rgba(96, 165, 250, .18); color: #bfdbfe; }\r\n\r\n.rule-code {\r\n  display: inline-flex;\r\n  align-items: center;\r\n  border-radius: 999px;\r\n  padding: 4px 8px;\r\n  background: rgba(255, 255, 255, .055);\r\n  font-size: 12px;\r\n}\r\n\r\n.json-note {\r\n  margin-top: -6px;\r\n}\r\n\r\n.explain-grid {\r\n  display: grid;\r\n  grid-template-columns: repeat(2, minmax(0, 1fr));\r\n  gap: 14px;\r\n  margin: 16px 0;\r\n}\r\n\r\n.explain-grid > div {\r\n  border-radius: 18px;\r\n  padding: 14px;\r\n  background: rgba(255,255,255,.045);\r\n}\r\n\r\nsummary {\r\n  color: #bbf7d0;\r\n  font-weight: 800;\r\n  cursor: pointer;\r\n}\r\n\r\n.component-list { margin-top: 14px; }\r\n.component strong,\r\n.component span { display: block; overflow-wrap: anywhere; }\r\n\r\n.action { display: grid; grid-template-columns: auto 1fr; gap: 16px; }\r\n.priority {\r\n  display: grid;\r\n  place-items: center;\r\n  width: 52px;\r\n  height: 52px;\r\n  border-radius: 18px;\r\n  background: rgba(52, 211, 153, .16);\r\n  color: #bbf7d0;\r\n  font-weight: 900;\r\n}\r\n\r\n.compact { justify-content: flex-start; }\r\n\r\npre {\r\n  overflow: auto;\r\n  max-height: 72vh;\r\n  border-radius: 20px;\r\n  padding: 18px;\r\n  background: #020617;\r\n  border: 1px solid var(--border);\r\n  color: #d1fae5;\r\n}\r\n\r\n.empty {\r\n  padding: 44px;\r\n  text-align: center;\r\n}\r\n\r\n@media (max-width: 1100px) {\r\n  .hero,\r\n  .panel-grid,\r\n  .explain-grid {\r\n    grid-template-columns: 1fr;\r\n  }\r\n\r\n  .score-grid {\r\n    grid-template-columns: repeat(2, 1fr);\r\n  }\r\n}\r\n\r\n@media (max-width: 700px) {\r\n  .shell { width: min(100% - 24px, 1440px); padding-top: 18px; }\r\n  .score-grid { grid-template-columns: 1fr; }\r\n  .toolbar { display: grid; }\r\n  .toolbar select { width: 100%; }\r\n}\r\n\r\n.profile-box {\r\n  display: grid;\r\n  gap: 12px;\r\n  border: 1px solid var(--border);\r\n  border-radius: 22px;\r\n  padding: 14px;\r\n  background: rgba(0,0,0,.14);\r\n}\r\n\r\n.profile-box h2 {\r\n  margin: 0;\r\n  font-size: 16px;\r\n}\r\n\r\n.profile-grid {\r\n  display: grid;\r\n  grid-template-columns: repeat(2, minmax(0, 1fr));\r\n  gap: 12px;\r\n}\r\n\r\n.radio-group {\r\n  display: flex;\r\n  flex-wrap: wrap;\r\n  gap: 8px;\r\n}\r\n\r\n.radio-card {\r\n  display: inline-flex;\r\n  align-items: center;\r\n  gap: 8px;\r\n  padding: 10px 12px;\r\n  border: 1px solid var(--border);\r\n  border-radius: 14px;\r\n  color: var(--text);\r\n  background: rgba(0, 0, 0, .18);\r\n  text-transform: none;\r\n  letter-spacing: 0;\r\n  cursor: pointer;\r\n}\r\n\r\n.radio-card:has(input:checked) {\r\n  color: #bbf7d0;\r\n  background: rgba(52, 211, 153, .18);\r\n  border-color: rgba(187, 247, 208, .28);\r\n}\r\n\r\n.radio-card input {\r\n  width: auto;\r\n}\r\n\r\n.check-row {\r\n  display: flex;\r\n  align-items: center;\r\n  gap: 10px;\r\n  text-transform: none;\r\n  letter-spacing: 0;\r\n  color: var(--text);\r\n}\r\n\r\n.check-row input {\r\n  width: auto;\r\n}\r\n\r\n.readiness[data-status=\"NOT_READY\"] {\r\n  box-shadow: inset 0 0 0 1px rgba(251, 113, 133, .32), 0 22px 90px rgba(0, 0, 0, .26);\r\n}\r\n\r\n.readiness[data-status=\"READY_WITH_WARNINGS\"] {\r\n  box-shadow: inset 0 0 0 1px rgba(251, 191, 36, .28), 0 22px 90px rgba(0, 0, 0, .26);\r\n}\r\n\r\n.readiness[data-status=\"READY\"] {\r\n  box-shadow: inset 0 0 0 1px rgba(52, 211, 153, .28), 0 22px 90px rgba(0, 0, 0, .26);\r\n}\r\n\r\n.release-grid,\r\n.gate-grid,\r\n.area-grid {\r\n  display: grid;\r\n  grid-template-columns: repeat(2, minmax(0, 1fr));\r\n  gap: 14px;\r\n}\r\n\r\n.gate-card,\r\n.area-card {\r\n  border: 1px solid var(--border);\r\n  background: rgba(0,0,0,.16);\r\n  border-radius: 22px;\r\n  padding: 18px;\r\n}\r\n\r\n.gate-card[data-status=\"FAIL\"],\r\n.area-card[data-status=\"BLOCKED\"] {\r\n  border-color: rgba(251, 113, 133, .44);\r\n}\r\n\r\n.gate-card[data-status=\"WARNING\"],\r\n.area-card[data-status=\"ATTENTION\"] {\r\n  border-color: rgba(251, 191, 36, .36);\r\n}\r\n\r\n.gate-card[data-status=\"PASS\"],\r\n.area-card[data-status=\"OK\"] {\r\n  border-color: rgba(52, 211, 153, .3);\r\n}\r\n\r\n.area-metrics,\r\n.chip-list {\r\n  display: flex;\r\n  flex-wrap: wrap;\r\n  gap: 8px;\r\n}\r\n\r\n.area-metrics span,\r\n.chip-list span {\r\n  display: inline-flex;\r\n  border-radius: 999px;\r\n  padding: 6px 10px;\r\n  color: var(--text);\r\n  background: rgba(255, 255, 255, .06);\r\n  border: 1px solid var(--border);\r\n  font-size: 13px;\r\n}\r\n\r\n@media (max-width: 1100px) {\r\n  .profile-grid,\r\n  .release-grid,\r\n  .gate-grid,\r\n  .area-grid {\r\n    grid-template-columns: 1fr;\r\n  }\r\n}\r\n\r\n.advisor-score {\r\n  box-shadow: inset 0 0 0 1px rgba(52, 211, 153, .2), 0 22px 90px rgba(0, 0, 0, .26);\r\n}\r\n\r\n.advisor-panel .panel-title {\r\n  align-items: flex-start;\r\n}\r\n\r\n.advisor-panel .panel-title p {\r\n  margin-bottom: 0;\r\n}\r\n\r\n.type-code {\r\n  color: #bbf7d0;\r\n}\r\n\r\n.inline-empty {\r\n  box-shadow: none;\r\n  background: rgba(0, 0, 0, .14);\r\n  padding: 28px;\r\n}\r\n\r\n.type-card,\r\n.advisor-finding {\r\n  position: relative;\r\n  overflow: hidden;\r\n}\r\n\r\n.type-card::before,\r\n.advisor-finding::before {\r\n  content: '';\r\n  position: absolute;\r\n  inset: 0 auto 0 0;\r\n  width: 4px;\r\n  background: linear-gradient(180deg, var(--brand), transparent);\r\n  opacity: .75;\r\n}\r\n\r\n.score-grid .score-card {\r\n  transition: transform .18s ease, border-color .18s ease, background .18s ease;\r\n}\r\n\r\n.score-grid .score-card:hover,\r\n.gate-card:hover,\r\n.area-card:hover,\r\n.finding:hover {\r\n  transform: translateY(-2px);\r\n  border-color: rgba(187, 247, 208, .28);\r\n  background: linear-gradient(145deg, rgba(255, 255, 255, .11), rgba(255, 255, 255, .05));\r\n}\r\n\r\n.rule-code {\r\n  border: 1px solid var(--border);\r\n  border-radius: 999px;\r\n  padding: 4px 8px;\r\n  background: rgba(255,255,255,.045);\r\n}\r\n\r\n\r\n.code-evidence {\r\n  margin: 10px 0 0;\r\n  padding: 12px 14px;\r\n  overflow-x: auto;\r\n  border: 1px solid rgba(15, 23, 42, 0.12);\r\n  border-radius: 12px;\r\n  background: #0f172a;\r\n  color: #e5e7eb;\r\n  font-size: 0.82rem;\r\n  line-height: 1.55;\r\n  white-space: pre;\r\n}\r\n\r\n.code-evidence code {\r\n  font-family: \"JetBrains Mono\", \"Fira Code\", Consolas, monospace;\r\n}\r\n\r\n/* Decision-first report UX -------------------------------------------------- */\r\n.score-grid {\r\n  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));\r\n}\r\n\r\n.main-score {\r\n  grid-column: span 2;\r\n}\r\n\r\n.decision-board {\r\n  border: 1px solid var(--border);\r\n  background:\r\n    radial-gradient(circle at top left, rgba(52, 211, 153, .16), transparent 36%),\r\n    linear-gradient(145deg, rgba(255,255,255,.10), rgba(255,255,255,.045));\r\n  box-shadow: 0 22px 90px rgba(0, 0, 0, .24);\r\n  backdrop-filter: blur(18px);\r\n  border-radius: 28px;\r\n  padding: 22px;\r\n  margin: 0 0 18px;\r\n}\r\n\r\n.decision-board-title {\r\n  display: flex;\r\n  justify-content: space-between;\r\n  gap: 16px;\r\n  margin-bottom: 16px;\r\n}\r\n\r\n.decision-board-title h2 {\r\n  margin-bottom: 6px;\r\n}\r\n\r\n.decision-board-title p {\r\n  color: var(--muted);\r\n  margin-bottom: 0;\r\n  line-height: 1.55;\r\n}\r\n\r\n.decision-lanes {\r\n  display: grid;\r\n  grid-template-columns: repeat(5, minmax(0, 1fr));\r\n  gap: 12px;\r\n}\r\n\r\n.decision-lane {\r\n  position: relative;\r\n  overflow: hidden;\r\n  border: 1px solid var(--border);\r\n  background: rgba(0,0,0,.18);\r\n  border-radius: 22px;\r\n  padding: 16px;\r\n  min-height: 178px;\r\n  display: flex;\r\n  flex-direction: column;\r\n  gap: 10px;\r\n}\r\n\r\n.decision-lane::before {\r\n  content: '';\r\n  position: absolute;\r\n  inset: 0 auto 0 0;\r\n  width: 5px;\r\n  background: rgba(148, 163, 184, .8);\r\n}\r\n\r\n.decision-lane[data-lane=\"BLOCKERS\"]::before { background: #fb7185; }\r\n.decision-lane[data-lane=\"IMPORTANT\"]::before { background: #fbbf24; }\r\n.decision-lane[data-lane=\"IMPROVEMENTS\"]::before { background: #60a5fa; }\r\n.decision-lane[data-lane=\"ADVISOR\"]::before { background: #34d399; }\r\n.decision-lane[data-lane=\"INFORMATION\"]::before { background: #a78bfa; }\r\n\r\n.decision-lane span {\r\n  color: var(--muted);\r\n  font-size: 12px;\r\n  font-weight: 900;\r\n  letter-spacing: .08em;\r\n  text-transform: uppercase;\r\n  padding-left: 4px;\r\n}\r\n\r\n.decision-lane strong {\r\n  font-size: 42px;\r\n  line-height: 1;\r\n  letter-spacing: -.05em;\r\n}\r\n\r\n.decision-lane p {\r\n  color: var(--muted);\r\n  line-height: 1.45;\r\n  margin-bottom: auto;\r\n}\r\n\r\n.finding[data-decision=\"ADVISOR\"],\r\n.advisor-finding {\r\n  border-color: rgba(52, 211, 153, .26);\r\n}\r\n\r\n.code-evidence {\r\n  margin: 12px 0 0;\r\n  padding: 14px;\r\n  border-radius: 16px;\r\n  border: 1px solid rgba(148, 163, 184, .24);\r\n  background: rgba(2, 6, 23, .72);\r\n  overflow: auto;\r\n  color: #dbeafe;\r\n  line-height: 1.55;\r\n  font-size: 13px;\r\n}\r\n\r\n.code-evidence code {\r\n  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', monospace;\r\n  white-space: pre;\r\n}\r\n\r\n@media (max-width: 1100px) {\r\n  .decision-lanes {\r\n    grid-template-columns: repeat(2, minmax(0, 1fr));\r\n  }\r\n\r\n  .main-score {\r\n    grid-column: span 1;\r\n  }\r\n}\r\n\r\n@media (max-width: 720px) {\r\n  .decision-lanes {\r\n    grid-template-columns: 1fr;\r\n  }\r\n}\r\n\r\n/* Action-oriented finding cards -------------------------------------------- */\r\n.finding-guidance,\r\n.advisor-flow {\r\n  display: grid;\r\n  grid-template-columns: repeat(3, minmax(0, 1fr));\r\n  gap: 12px;\r\n  margin: 16px 0;\r\n}\r\n\r\n.advisor-flow {\r\n  grid-template-columns: repeat(2, minmax(0, 1fr));\r\n}\r\n\r\n.guidance-item,\r\n.advisor-flow > div {\r\n  border: 1px solid rgba(148, 163, 184, .18);\r\n  border-radius: 18px;\r\n  padding: 14px;\r\n  background: rgba(255,255,255,.045);\r\n}\r\n\r\n.guidance-item span,\r\n.advisor-flow span,\r\n.advisor-group-title span {\r\n  display: inline-flex;\r\n  color: #bbf7d0;\r\n  font-size: 12px;\r\n  font-weight: 900;\r\n  letter-spacing: .08em;\r\n  text-transform: uppercase;\r\n  margin-bottom: 8px;\r\n}\r\n\r\n.guidance-item.detected { border-color: rgba(96, 165, 250, .32); }\r\n.guidance-item.risk { border-color: rgba(251, 191, 36, .28); }\r\n.guidance-item.solution { border-color: rgba(52, 211, 153, .28); }\r\n\r\n.doc-link {\r\n  display: inline-flex;\r\n  align-items: center;\r\n  gap: 8px;\r\n  width: fit-content;\r\n  margin: 0 0 14px;\r\n  padding: 8px 12px;\r\n  border-radius: 999px;\r\n  color: #bbf7d0;\r\n  background: rgba(52, 211, 153, .12);\r\n  border: 1px solid rgba(52, 211, 153, .28);\r\n  text-decoration: none;\r\n  font-weight: 800;\r\n}\r\n\r\n.before-after {\r\n  display: grid;\r\n  grid-template-columns: repeat(2, minmax(0, 1fr));\r\n  gap: 12px;\r\n  margin: 10px 0 16px;\r\n}\r\n\r\n.before-after h4 {\r\n  color: var(--muted);\r\n  margin: 0 0 8px;\r\n  font-size: 12px;\r\n  text-transform: uppercase;\r\n  letter-spacing: .08em;\r\n}\r\n\r\n.advisor-group-list {\r\n  display: grid;\r\n  gap: 18px;\r\n}\r\n\r\n.advisor-group {\r\n  border: 1px solid rgba(52, 211, 153, .20);\r\n  border-radius: 24px;\r\n  padding: 16px;\r\n  background: rgba(0,0,0,.12);\r\n}\r\n\r\n.advisor-group-title {\r\n  display: flex;\r\n  align-items: baseline;\r\n  justify-content: space-between;\r\n  gap: 12px;\r\n  margin-bottom: 12px;\r\n}\r\n\r\n.advisor-group-title h3 {\r\n  margin: 0;\r\n}\r\n\r\n@media (max-width: 1100px) {\r\n  .finding-guidance,\r\n  .advisor-flow,\r\n  .before-after {\r\n    grid-template-columns: 1fr;\r\n  }\r\n}\r\n\r\n/* Problem experience -------------------------------------------------------- */\r\n.problem-area-strip {\r\n  display: flex;\r\n  align-items: center;\r\n  gap: 10px;\r\n  flex-wrap: wrap;\r\n  margin: 0 0 18px;\r\n  padding: 12px;\r\n  border: 1px solid rgba(148, 163, 184, .18);\r\n  border-radius: 20px;\r\n  background: rgba(255, 255, 255, .035);\r\n}\r\n\r\n.strip-title {\r\n  color: var(--muted);\r\n  font-size: 12px;\r\n  font-weight: 900;\r\n  text-transform: uppercase;\r\n  letter-spacing: .08em;\r\n  margin-right: 4px;\r\n}\r\n\r\n.type-pill {\r\n  display: inline-flex;\r\n  align-items: center;\r\n  gap: 10px;\r\n  border: 1px solid rgba(148, 163, 184, .20);\r\n  border-radius: 999px;\r\n  padding: 8px 12px;\r\n  color: var(--text);\r\n  background: rgba(15, 23, 42, .46);\r\n  cursor: pointer;\r\n}\r\n\r\n.type-pill.active,\r\n.type-pill:hover {\r\n  border-color: rgba(52, 211, 153, .42);\r\n  background: rgba(52, 211, 153, .14);\r\n}\r\n\r\n.type-pill strong {\r\n  display: inline-flex;\r\n  min-width: 26px;\r\n  justify-content: center;\r\n  border-radius: 999px;\r\n  padding: 2px 7px;\r\n  background: rgba(255, 255, 255, .08);\r\n}\r\n\r\n.active-filter {\r\n  margin: 0 0 14px;\r\n  padding: 10px 14px;\r\n  border-left: 4px solid #34d399;\r\n  border-radius: 14px;\r\n  background: rgba(52, 211, 153, .10);\r\n  color: var(--muted);\r\n}\r\n\r\n.finding-list.grouped {\r\n  display: grid;\r\n  gap: 22px;\r\n}\r\n\r\n.problem-group {\r\n  display: grid;\r\n  gap: 14px;\r\n}\r\n\r\n.problem-group-head {\r\n  display: flex;\r\n  align-items: center;\r\n  justify-content: space-between;\r\n  padding: 12px 16px;\r\n  border-radius: 18px;\r\n  background: linear-gradient(90deg, rgba(52, 211, 153, .14), rgba(15, 23, 42, .18));\r\n  border: 1px solid rgba(52, 211, 153, .18);\r\n}\r\n\r\n.problem-group-head span {\r\n  color: #bbf7d0;\r\n  font-size: 12px;\r\n  font-weight: 900;\r\n  letter-spacing: .08em;\r\n  text-transform: uppercase;\r\n}\r\n\r\n.problem-group-head h3 {\r\n  margin: 4px 0 0;\r\n}\r\n\r\n.finding .finding-guidance.action-oriented {\r\n  grid-template-columns: 1fr;\r\n}\r\n\r\n.finding .guidance-item {\r\n  padding: 12px 14px;\r\n}\r\n\r\n.finding .guidance-item p {\r\n  margin: 0;\r\n  line-height: 1.55;\r\n}\r\n\r\n.before-after.compact {\r\n  margin-top: 14px;\r\n}\r\n\r\n.before-after.compact .code-evidence {\r\n  max-height: 260px;\r\n  overflow: auto;\r\n}\r\n\r\n/* =========================\r\n   Spring Guardian brand\r\n   ========================= */\r\n.hero-copy {\r\n  display: flex;\r\n  flex-direction: column;\r\n  justify-content: space-between;\r\n  min-width: 0;\r\n}\r\n\r\n.brand-lockup {\r\n  display: inline-flex;\r\n  align-items: center;\r\n  gap: 14px;\r\n  width: fit-content;\r\n  padding: 10px 14px 10px 10px;\r\n  margin-bottom: 18px;\r\n  border: 1px solid rgba(187, 247, 208, .18);\r\n  border-radius: 24px;\r\n  background: linear-gradient(135deg, rgba(15, 23, 42, .72), rgba(6, 78, 59, .34));\r\n  box-shadow: 0 18px 70px rgba(0, 0, 0, .22);\r\n}\r\n\r\n.brand-logo {\r\n  width: 68px;\r\n  height: 68px;\r\n  flex: 0 0 auto;\r\n  border-radius: 18px;\r\n  box-shadow: 0 14px 36px rgba(16, 185, 129, .18);\r\n}\r\n\r\n.brand-text {\r\n  display: grid;\r\n  gap: 3px;\r\n}\r\n\r\n.brand-name {\r\n  color: #ecfdf5;\r\n  font-weight: 950;\r\n  font-size: 20px;\r\n  letter-spacing: -.04em;\r\n}\r\n\r\n.brand-subtitle {\r\n  color: #86efac;\r\n  font-size: 11px;\r\n  font-weight: 900;\r\n  letter-spacing: .16em;\r\n  text-transform: uppercase;\r\n}\r\n\r\n.hero-brand-card {\r\n  width: min(100%, 720px);\r\n  margin-top: 24px;\r\n  border-radius: 34px;\r\n  overflow: hidden;\r\n  border: 1px solid rgba(187, 247, 208, .14);\r\n  box-shadow: 0 22px 90px rgba(0, 0, 0, .28);\r\n}\r\n\r\n.hero-brand-card img {\r\n  display: block;\r\n  width: 100%;\r\n  height: auto;\r\n}\r\n\r\n.empty-logo {\r\n  width: 116px;\r\n  height: 116px;\r\n  margin-bottom: 18px;\r\n  border-radius: 28px;\r\n  box-shadow: 0 18px 60px rgba(16, 185, 129, .2);\r\n}\r\n\r\n@media (max-width: 700px) {\r\n  .brand-lockup {\r\n    width: 100%;\r\n  }\r\n\r\n  .brand-logo {\r\n    width: 56px;\r\n    height: 56px;\r\n  }\r\n\r\n  .brand-name {\r\n    font-size: 18px;\r\n  }\r\n\r\n  .brand-subtitle {\r\n    font-size: 10px;\r\n    letter-spacing: .1em;\r\n  }\r\n}\r\n\r\n\r\n.real-evidence {\r\n  margin: 18px 0;\r\n  padding: 14px 16px;\r\n  border: 1px solid rgba(148, 163, 184, 0.28);\r\n  border-radius: 16px;\r\n  background: rgba(15, 23, 42, 0.35);\r\n}\r\n\r\n.real-evidence h4 {\r\n  margin: 0 0 8px;\r\n  color: #bbf7d0;\r\n  font-size: 0.78rem;\r\n  letter-spacing: 0.06em;\r\n  text-transform: uppercase;\r\n}\r\n\r\n.real-evidence p {\r\n  margin: 6px 0;\r\n}\r\n\r\n/* Visual polish pass ------------------------------------------------------- */\r\n.hero {\r\n  position: relative;\r\n}\r\n\r\n.hero::before {\r\n  content: '';\r\n  position: absolute;\r\n  inset: -24px -24px auto -24px;\r\n  height: 420px;\r\n  pointer-events: none;\r\n  background:\r\n    radial-gradient(circle at 16% 18%, rgba(52, 211, 153, .22), transparent 34%),\r\n    radial-gradient(circle at 74% 10%, rgba(96, 165, 250, .18), transparent 30%);\r\n  filter: blur(4px);\r\n  z-index: -1;\r\n}\r\n\r\n.hero-copy,\r\n.scan-card,\r\n.score-card,\r\n.panel,\r\n.decision-board,\r\n.impact-card,\r\n.severity-map {\r\n  position: relative;\r\n  overflow: hidden;\r\n}\r\n\r\n.hero-copy::after,\r\n.scan-card::after,\r\n.panel::after,\r\n.score-card::after,\r\n.impact-card::after,\r\n.severity-map::after,\r\n.decision-board::after {\r\n  content: '';\r\n  position: absolute;\r\n  inset: 0;\r\n  pointer-events: none;\r\n  background: linear-gradient(120deg, rgba(255,255,255,.16), transparent 28%, transparent 72%, rgba(255,255,255,.055));\r\n  opacity: .38;\r\n}\r\n\r\n.brand-lockup {\r\n  display: inline-flex;\r\n  align-items: center;\r\n  gap: 14px;\r\n  padding: 10px 14px;\r\n  margin-bottom: 28px;\r\n  border: 1px solid rgba(187, 247, 208, .18);\r\n  border-radius: 22px;\r\n  background: rgba(2, 6, 23, .38);\r\n  box-shadow: 0 18px 60px rgba(0,0,0,.22);\r\n}\r\n\r\n.brand-logo {\r\n  width: 52px;\r\n  height: 52px;\r\n  filter: drop-shadow(0 14px 26px rgba(16,185,129,.25));\r\n}\r\n\r\n.brand-text {\r\n  display: grid;\r\n  gap: 2px;\r\n}\r\n\r\n.brand-name {\r\n  font-size: 18px;\r\n  font-weight: 950;\r\n  letter-spacing: -.03em;\r\n}\r\n\r\n.brand-subtitle {\r\n  color: var(--muted);\r\n  font-size: 12px;\r\n  font-weight: 800;\r\n  letter-spacing: .12em;\r\n  text-transform: uppercase;\r\n}\r\n\r\n.impact-hub {\r\n  display: grid;\r\n  grid-template-columns: 1.25fr repeat(3, 1fr);\r\n  gap: 14px;\r\n  margin: 0 0 18px;\r\n}\r\n\r\n.impact-card,\r\n.severity-map {\r\n  border: 1px solid var(--border);\r\n  border-radius: 28px;\r\n  background:\r\n    radial-gradient(circle at top left, rgba(52,211,153,.14), transparent 42%),\r\n    linear-gradient(145deg, rgba(255,255,255,.095), rgba(255,255,255,.04));\r\n  box-shadow: 0 22px 90px rgba(0,0,0,.22);\r\n  backdrop-filter: blur(18px);\r\n}\r\n\r\n.impact-card {\r\n  min-height: 156px;\r\n  padding: 20px;\r\n  display: flex;\r\n  flex-direction: column;\r\n  gap: 10px;\r\n}\r\n\r\n.impact-card span,\r\n.severity-row-head span {\r\n  color: var(--muted);\r\n  font-size: 12px;\r\n  font-weight: 900;\r\n  letter-spacing: .12em;\r\n  text-transform: uppercase;\r\n}\r\n\r\n.impact-card strong {\r\n  font-size: clamp(20px, 2vw, 30px);\r\n  letter-spacing: -.04em;\r\n  line-height: 1.05;\r\n}\r\n\r\n.impact-card p {\r\n  margin: 0;\r\n  color: var(--muted);\r\n}\r\n\r\n.primary-impact {\r\n  border-color: rgba(52,211,153,.34);\r\n  background:\r\n    radial-gradient(circle at 22% 0%, rgba(52,211,153,.28), transparent 44%),\r\n    linear-gradient(145deg, rgba(16,185,129,.16), rgba(255,255,255,.055));\r\n}\r\n\r\n.scan-identity strong,\r\n.scan-identity p {\r\n  overflow-wrap: anywhere;\r\n}\r\n\r\n.severity-map {\r\n  display: grid;\r\n  grid-template-columns: .72fr 1.28fr;\r\n  gap: 22px;\r\n  align-items: center;\r\n  margin: 0 0 18px;\r\n  padding: 22px;\r\n}\r\n\r\n.severity-map-copy h2 {\r\n  margin-bottom: 8px;\r\n  font-size: clamp(26px, 3vw, 42px);\r\n  letter-spacing: -.06em;\r\n}\r\n\r\n.severity-map-copy p {\r\n  margin: 0;\r\n  color: var(--muted);\r\n  line-height: 1.6;\r\n}\r\n\r\n.severity-bars {\r\n  display: grid;\r\n  gap: 12px;\r\n}\r\n\r\n.severity-row {\r\n  display: grid;\r\n  gap: 8px;\r\n}\r\n\r\n.severity-row-head {\r\n  display: flex;\r\n  justify-content: space-between;\r\n  gap: 16px;\r\n}\r\n\r\n.severity-row-head strong {\r\n  font-size: 13px;\r\n}\r\n\r\n.severity-track {\r\n  height: 12px;\r\n  border-radius: 999px;\r\n  background: rgba(255,255,255,.075);\r\n  border: 1px solid rgba(255,255,255,.08);\r\n  overflow: hidden;\r\n}\r\n\r\n.severity-track i {\r\n  display: block;\r\n  height: 100%;\r\n  min-width: 4px;\r\n  border-radius: inherit;\r\n  background: linear-gradient(90deg, var(--brand), var(--brand-strong));\r\n  box-shadow: 0 0 26px rgba(52,211,153,.34);\r\n}\r\n\r\n.severity-row[data-severity=\"CRITICAL\"] .severity-track i {\r\n  background: linear-gradient(90deg, #fb7185, #ef4444);\r\n  box-shadow: 0 0 26px rgba(251,113,133,.34);\r\n}\r\n\r\n.severity-row[data-severity=\"MAJOR\"] .severity-track i {\r\n  background: linear-gradient(90deg, #fbbf24, #f97316);\r\n  box-shadow: 0 0 26px rgba(251,191,36,.28);\r\n}\r\n\r\n.severity-row[data-severity=\"MINOR\"] .severity-track i {\r\n  background: linear-gradient(90deg, #a78bfa, #6366f1);\r\n  box-shadow: 0 0 26px rgba(167,139,250,.28);\r\n}\r\n\r\n.tabs {\r\n  position: sticky;\r\n  top: 12px;\r\n  z-index: 10;\r\n  backdrop-filter: blur(24px);\r\n  box-shadow: 0 18px 70px rgba(0,0,0,.22);\r\n}\r\n\r\n.finding,\r\n.action,\r\n.component,\r\n.gate-card,\r\n.area-card,\r\n.decision-lane {\r\n  transition: transform .18s ease, border-color .18s ease, background .18s ease, box-shadow .18s ease;\r\n}\r\n\r\n.finding:hover,\r\n.action:hover,\r\n.component:hover,\r\n.gate-card:hover,\r\n.area-card:hover,\r\n.decision-lane:hover {\r\n  box-shadow: 0 18px 70px rgba(0,0,0,.18);\r\n}\r\n\r\n.primary,\r\n.ghost,\r\n.tabs button,\r\n.language-switch button,\r\n.mode-switch button,\r\n.radio-card {\r\n  transition: transform .16s ease, background .16s ease, border-color .16s ease, box-shadow .16s ease;\r\n}\r\n\r\n.primary:hover,\r\n.ghost:hover,\r\n.tabs button:hover,\r\n.language-switch button:hover,\r\n.mode-switch button:hover,\r\n.radio-card:hover {\r\n  transform: translateY(-1px);\r\n}\r\n\r\n.primary:active,\r\n.ghost:active,\r\n.tabs button:active,\r\n.language-switch button:active,\r\n.mode-switch button:active,\r\n.radio-card:active {\r\n  transform: translateY(0);\r\n}\r\n\r\n@media (max-width: 1180px) {\r\n  .impact-hub,\r\n  .severity-map {\r\n    grid-template-columns: 1fr 1fr;\r\n  }\r\n\r\n  .primary-impact {\r\n    grid-column: 1 / -1;\r\n  }\r\n}\r\n\r\n@media (max-width: 760px) {\r\n  .impact-hub,\r\n  .severity-map {\r\n    grid-template-columns: 1fr;\r\n  }\r\n\r\n  .main-score {\r\n    grid-column: auto;\r\n  }\r\n\r\n  .brand-lockup {\r\n    width: 100%;\r\n  }\r\n}\r\n\n.module-grid {\n  display: grid;\n  gap: 16px;\n  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));\n}\n\n.module-card {\n  border: 1px solid rgba(148, 163, 184, .18);\n  border-radius: 24px;\n  padding: 18px;\n  min-height: 150px;\n  background: linear-gradient(145deg, rgba(15, 23, 42, .72), rgba(15, 118, 110, .12));\n  box-shadow: inset 0 1px 0 rgba(255,255,255,.05);\n  transition: transform .18s ease, border-color .18s ease, box-shadow .18s ease;\n}\n\n.module-card[data-active=\"true\"] {\n  border-color: rgba(52, 211, 153, .5);\n  background: linear-gradient(145deg, rgba(20, 83, 45, .42), rgba(15, 23, 42, .78));\n}\n\n.module-card:hover {\n  transform: translateY(-2px);\n  box-shadow: 0 20px 80px rgba(0, 0, 0, .2);\n}\n\n.module-card small {\n  display: inline-flex;\n  margin-top: 12px;\n  color: var(--muted);\n  font-weight: 700;\n}\n\n/* Automatic Spring-centric scan card --------------------------------------- */\n.auto-analysis-box {\n  display: grid;\n  gap: 16px;\n  border: 1px solid rgba(52, 211, 153, .28);\n  border-radius: 24px;\n  padding: 18px;\n  background:\n    radial-gradient(circle at top left, rgba(52, 211, 153, .18), transparent 42%),\n    linear-gradient(145deg, rgba(15, 118, 110, .18), rgba(2, 6, 23, .18));\n}\n\n.auto-analysis-head span,\n.scan-ready-strip span {\n  display: block;\n  color: var(--brand);\n  font-size: 12px;\n  font-weight: 900;\n  text-transform: uppercase;\n  letter-spacing: .12em;\n  margin-bottom: 6px;\n}\n\n.auto-analysis-head strong {\n  display: block;\n  font-size: 22px;\n  letter-spacing: -.04em;\n  margin-bottom: 8px;\n}\n\n.auto-analysis-head p {\n  color: var(--muted);\n  margin: 0;\n  line-height: 1.55;\n}\n\n.analysis-flow {\n  display: grid;\n  grid-template-columns: repeat(2, minmax(0, 1fr));\n  gap: 10px;\n}\n\n.analysis-flow > div {\n  min-height: 92px;\n  border: 1px solid rgba(148, 163, 184, .20);\n  border-radius: 18px;\n  padding: 12px;\n  background: rgba(0, 0, 0, .18);\n}\n\n.analysis-flow b {\n  display: inline-grid;\n  place-items: center;\n  width: 34px;\n  height: 34px;\n  border-radius: 12px;\n  color: #042117;\n  background: linear-gradient(135deg, var(--brand), #bbf7d0);\n  font-size: 13px;\n  margin-bottom: 10px;\n}\n\n.analysis-flow span {\n  display: block;\n  color: var(--text);\n  font-size: 13px;\n  line-height: 1.45;\n}\n\n.scan-ready-strip {\n  display: grid;\n  gap: 5px;\n  border: 1px solid rgba(187, 247, 208, .22);\n  border-radius: 20px;\n  padding: 14px 16px;\n  background: rgba(52, 211, 153, .10);\n}\n\n.scan-ready-strip strong {\n  font-size: 18px;\n}\n\n.scan-card .primary {\n  min-height: 58px;\n  font-size: 16px;\n  letter-spacing: .02em;\n  box-shadow: 0 18px 42px rgba(16, 185, 129, .24);\n}\n\n.mode-switch {\n  display: grid;\n  grid-template-columns: repeat(3, minmax(0, 1fr));\n}\n\n.mode-switch button {\n  min-height: 48px;\n  font-weight: 800;\n}\n\n.scan-card input[type=\"text\"],\n.scan-card input[type=\"file\"] {\n  min-height: 54px;\n  font-weight: 700;\n}\n\n@media (max-width: 520px) {\n  .analysis-flow,\n  .mode-switch {\n    grid-template-columns: 1fr;\n  }\n}\n"] }]
    }], () => [{ type: i1.SpringGuardianApiService }], null); })();
(() => { (typeof ngDevMode === "undefined" || ngDevMode) && i0.ɵsetClassDebugInfo(AppComponent, { className: "AppComponent", filePath: "src/app/app.component.ts", lineNumber: 414 }); })();
//# sourceMappingURL=app.component.js.map