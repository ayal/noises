(ns insane-noises.core
  (:use overtone.live
        overtone.inst.piano
        overtone.inst.sampled-piano))

;; M-x cider-jack-in



(defn piece [start] )

(defn pno [n] (piano n
                 1 100 0.00001 0.0000001 0.000000001
                 0.00001 0.000001 0.00000001 0.000000000001 0.2 0.5
                 0.1 0.001 0.0001))

(definst plucked-string [note 60 amp 0.8 dur 1.5 decay 10 coef 0.005 gate 0.001]
  (let [freq   (midicps note)
        noize  (* 0.1 (white-noise))
        dly    (/ 1.0 freq)
        plk    (pluck noize gate dly dly decay coef)
        dist   (distort plk)
        filt   (rlpf dist (* 2 freq) 1)
        clp    (clip2 filt 0.1)
        reverb (free-verb clp 0.01 0.008 0.0002)]
    (* amp (env-gen (perc 0.001 dur)) reverb)))

(definst plucked-string [note 60 amp 0.8 dur 2 decay 30 coef 0.3 gate 1]
  (let [freq   (midicps note)
        noize  (* 0.8 (white-noise))
        dly    (/ 1.0 freq)
        plk    (pluck noize gate dly dly decay coef)
        dist   (distort plk)
        filt   (rlpf dist (* 12 freq) 0.6)
        clp    (clip2 filt 0.8)
        reverb (free-verb clp 0.4 0.8 0.2)]
    (* amp (env-gen (perc 0.0001 dur)) reverb)))

(stop)

; real thing
(player (now) 200
        (flatten
         (take 5
               (repeat
                [(arp E)
                 (arp B)
                 (arp Bdim)
                 (arp Bdim2)
                 (arp FSharpMin9)
                 (arp FSharpMin92)]))))


(stop)



(player (now) 140 [52 54 56 59 54 56 59 64 66   66 71 73 76 71 68 66])

(defn spno [n] (sampled-piano n 0.1 1 0    0 1 0.1 0))


(defn player
  [t speed notes]
  (let [n      (first notes)
        notes  (next notes)
        t-next (+ t speed)]
    (when n
      (at t
          (pno n)
      (apply-by t-next #'player [t-next speed notes])))))

(defn up[v n] (map (fn [a] (+ a 12*n)) v))

(defn down[v] (map (fn [a] (- a 12)) v))
(defn arp[c] [c (rest c) (up c) (up (rest c)) (reverse (up (rest c)))])
(def E [52 54 56 59]) 
(def B [54 59 63 66])
(def Bdim [50 53 59 62])
(def Bdim2 [50 53 56 59])
(def FSharpMin9 [54 57 61 64 68])
(def FSharpMin92 [45 54 57 60 64 68])

(defn arp2 [letter, type]
  (concat
   (sort   (chord (keyword (str letter "3")) type))
   (rest  (chord (keyword (str letter "3")) type))
   (reverse (sort  (chord (keyword (str letter "4")) type)))
   ))

(note :F#3)

(player (now) 100
        (concat 
         (sort (chord :E3 :m13))
         (sort (chord :E3 :m13)
        )))

(scale :E3 :major)

(chord :F7 :M7)

(note :G#3)
(note :F#3)54


(player (now) 130 (concat
                   
                   (sort (concat [(note :G#3)] (chord :E3 :major) [(note :F#3)]))
                   (sort (concat [(note :G#3)] (chord :E3 :major) [(note :F#3)]))
                   (sort (concat [(note :G#4)] (chord :E4 :major) ))
                   (reverse (sort (concat [(note :G#4)] (chord :E4 :major) [(note :C#4)])))

                   (chord :C3 :major)
                   (reverse (chord :C3 :major))
                   (chord :C4 :major)
                   (reverse (chord :C4 :major))

                   (chord :C3 :dim)
                   (reverse (chord :C3 :diminished))
                   (chord :C4 :diminished)
                   (reverse (chord :C4 :diminished))

                   (sort (concat (chord :C3 :diminished) [(note :G#2)]))
                   (chord :C3 :diminished)
                   (chord :C4 :diminished)
                   (reverse (chord :C4 :diminished))

                   (chord :F#3 :m9)
                   (chord :F#4 :m9)
                   (reverse (chord :F#4 :m9))

                   (replace-value (vec (sort (concat (chord :F#3 :m9)[(note :A3)])))(note :C#4) (note :C4))
                   (replace-value  (vec (chord :F#3 :m9)) (note :C#4) (note :C4))
                   (replace-value (vec  (chord :F#4 :m9)) (note :C#5) (note :C5))
                   (replace-value (vec (reverse (chord :F#4 :m9))) (note :C#5) (note :C5))

                   
                   ))

(defn replace-value [vec ov nv] (assoc vec (.indexOf vec ov) nv))


(replace-value [1 2 3] 3 10)

(player (now) 240 [52 54 56 59 54 56 59 64 66   66 71 73 76 71 68 66])
(player (now) 100 (flatten [(arp E)]))

(spno 50)


(stop)
;;;; dustep

(defsynth dubstep [bpm 120 wobble 2 note 30 snare-vol 1 kick-vol 1 v 1 out-bus 0]
 (let [trig (impulse:kr (/ bpm 120))
       freq (midicps note)
       swr (demand trig 0 (dseq [wobble] INF))
       sweep (lin-exp (lf-tri swr) -1 1 40 3000)
       wob (apply + (saw (* freq [0.99 1.01])))
       wob (lpf wob sweep)
       wob (* 0.8 (normalizer wob))
       wob (+ wob (bpf wob 1500 2))
       wob (+ wob (* 0.2 (g-verb wob 9 0.7 0.7)))

       kickenv (decay (t2a (demand (impulse:kr (/ bpm 30)) 0 (dseq [1 0 0 0 0 0 1 0 1 0 0 1 0 0 0 0] INF))) 0.7)
       kick (* (* kickenv 7) (sin-osc (+ 40 (* kickenv kickenv kickenv 200))))
       kick (clip2 kick 1)

       snare (* 3 (pink-noise) (apply + (* (decay (impulse (/ bpm 240) 0.5) [0.4 2]) [1 0.05])))
       snare (+ snare (bpf (* 4 snare) 2000))
       snare (clip2 snare 1)]

   (out out-bus    (* v (clip2 (+ wob (* kick-vol kick) (* snare-vol snare)) 1)))))

(dubstep 100 0 10 0 0 0.5)
(stop)

(defsynth bsss [bpm 120 wobble 2 note 40 out-bus 0]
 (let [trig (impulse:kr (/ bpm 120))
       freq (midicps note)
       swr (demand trig 0 (dseq [wobble] INF))
       sweep (lin-exp (lf-tri swr) -1 1 40 200)
       wob (apply + (saw (* freq [0.99 1.01])))
       wob (lpf wob sweep)
     

       ]

   (out out-bus    wob)))

(bsss 100 30)
(stop)


;;;;; PREBEN GOEZ TO ACAPULCODE

;; M-x cider-jack-in

(ns insane-noises.core
  (:use overtone.live
        overtone.inst.piano
        overtone.inst.sampled-piano))

(definst sn [freq 120 a 0.1 b 1]
   (* (env-gen (perc a b) :action FREE)
      (sin-osc freq)))


(definst lpsn [freq 120 a 0.1 b 0.1]
   (* (env-gen (perc a b) :action FREE)
      (* 1 (normalizer (lpf (hpf (sin-osc freq) 500) 10)))))

(lpsn 80 0 3)

(definst sq [freq 120]
   (* (env-gen (perc 0.05 0.1) :action FREE)
      (square freq)))


(defn bplayer
  [t speed notes]
  (let [n      (first notes)
        notes  (next notes)
        t-next (+ t speed)]
    (when n
      (at t (lpsn n 0.1 0.6)
          (apply-by t-next #'bplayer [t-next speed notes])))))

(bplayer (now) 500
         (map (fn [a] (* (+ a 20) 2))
              (take 1000 (repeatedly #(rand-int 30))))
         )



(stop)

(defn player
  [t speed notes]
  (let [n      (first notes)
        notes  (next notes)
        t-next (+ t speed)]
    (when n
      (at t
         (lpsn (midi->hz (- n 24))) 
         )
      (at t
         (lpsn (midi->hz (- n 12))) 
         )
      (at t
         (sq (midi->hz n)) 
         )
      (at t
         (lpsn (midi->hz (+ n 12))) 
         )
      (apply-by t-next #'player [t-next speed notes]))))

;;

;;;;





;; (sampled-piano (note n))


(def F2 [40 41 45 48])
(def Fmaj7 [41 45 48 52])

(def Eb2 (map (fn [a] (- a 2)) F2))
(def Ebmaj7 (map (fn [a] (- a 2)) Fmaj7))

(def Ab65 [48 51 55 56])
(def Ab43 [51 55 56 60])

(def Db65 (map (fn [a] (- a 7)) Ab65))
(def Db43 (map (fn [a] (- a 7)) Ab43))

(defn up[v n]
  (map (fn [a]
         (+ a (- (* 12 n) 0))) v))

(player (now) 85
        (flatten
         [
           [(up F2 1) (up F2 2) (up F2 3) (up F2 4)]
          [(up (reverse Fmaj7) 4) (up (reverse Fmaj7) 3) (up (reverse Fmaj7) 2) (up (reverse Fmaj7) 1)]

[(up F2 1) (up F2 2) (up F2 3) (up F2 4)]
          [(up (reverse Fmaj7) 4) (up (reverse Fmaj7) 3) (up (reverse Fmaj7) 2) (up (reverse Fmaj7) 1)]
          
          
          [(up Eb2 1) (up Eb2 2) (up Eb2 3) (up Eb2 4)]
          [(up (reverse Ebmaj7) 4) (up (reverse Ebmaj7) 3) (up (reverse Ebmaj7) 2) (up (reverse Ebmaj7) 1)]

          [(up Eb2 1) (up Eb2 2) (up Eb2 3) (up Eb2 4)]
          [(up (reverse Ebmaj7) 4) (up (reverse Ebmaj7) 3) (up (reverse Ebmaj7) 2) (up (reverse Ebmaj7) 1)]

          
          [(up Ab65 1) (up Ab65 2) (up Ab65 3) (up Ab65 4)]
          [(up (reverse Ab43) 4) (up (reverse Ab43) 3) (up (reverse Ab43) 2) (up (reverse Ab43) 1)]


                    [(up Ab65 1) (up Ab65 2) (up Ab65 3) (up Ab65 4)]
          [(up (reverse Ab43) 4) (up (reverse Ab43) 3) (up (reverse Ab43) 2) (up (reverse Ab43) 1)]

          
          [(up Db65 1) (up Db65 2) (up Db65 3) (up Db65 4)]
          [(up (reverse Db43) 4) (up (reverse Db43) 3) (up (reverse Db43) 2) (up (reverse Db43) 1)]
          

                    [(up Db65 1) (up Db65 2) (up Db65 3) (up Db65 4)]
          [(up (reverse Db43) 4) (up (reverse Db43) 3) (up (reverse Db43) 2) (up (reverse Db43) 1)]

          
          ]
         ))

