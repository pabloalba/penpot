(ns uxbox.ui.workspace.canvas.resize
  (:require-macros [uxbox.util.syntax :refer [define-once]])
  (:require [sablono.core :as html :refer-macros [html]]
            [rum.core :as rum]
            [beicon.core :as rx]
            [lentes.core :as l]
            [uxbox.rstore :as rs]
            [uxbox.shapes :as ush]
            [uxbox.data.workspace :as udw]
            [uxbox.data.shapes :as uds]
            [uxbox.ui.core :as uuc]
            [uxbox.ui.shapes.core :as uusc]
            [uxbox.ui.workspace.base :as uuwb]
            [uxbox.ui.mixins :as mx]
            [uxbox.util.geom.point :as gpt]
            [uxbox.util.dom :as dom]))

(define-once :resize-subscriptions
  (letfn [(init [{:keys [payload]}]
            (println payload)
            (let [stoper (->> uuc/actions-s
                              (rx/map :type)
                              (rx/pr-log "kaka:")
                              (rx/filter #(= :nothing %))
                              (rx/take 1))]
              (as-> uuwb/mouse-delta-s $
                (rx/take-until stoper $)
                (rx/subscribe
                 $ #(on-value payload %) nil on-complete))))

          (on-complete []
            (println "on-complete"))

          (on-value [{:keys [vid shape]} delta]
            (let [params {:vid vid :delta delta}]
              (rs/emit! (uds/update-vertex-position shape params))))]

    (as-> uuc/actions-s $
      (rx/dedupe $)
      (rx/filter #(= (:type %) :resize/shape) $)
      (rx/on-value $ init))))
